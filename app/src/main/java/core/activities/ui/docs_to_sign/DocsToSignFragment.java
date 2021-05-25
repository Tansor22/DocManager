package core.activities.ui.docs_to_sign;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.entity.Document;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.request.ChangeDocRequest;
import com.auth0.android.jwt.JWT;
import com.yuyakaido.android.cardstackview.*;
import core.activities.R;
import core.activities.ui.create_doc.CreateDocumentWizardActivity;
import core.activities.ui.docs_to_sign.model.Result;
import core.activities.ui.docs_to_sign.model.SignDocModel;
import core.activities.ui.docs_to_sign.swipe.DocStackAdapter;
import core.activities.ui.docs_to_sign.swipe.DocStackListener;
import core.activities.ui.docs_to_sign.swipe.SwipeItemModel;
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.view_docs.DocumentsViewHolder;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;

public class DocsToSignFragment extends Fragment implements Traceable, UserMessageShower {
    private CardStackLayoutManager manager;
    private DocStackAdapter adapter;
    private CardStackView cardStackView;
    private SignDocModel model;
    private boolean needUpdate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_docs_to_sign, container, false);
        cardStackView = root.findViewById(R.id.cardStackView);
        model = new ViewModelProvider(this).get(SignDocModel.class);
        model.getResult().observe(getViewLifecycleOwner(), self -> {
            final Result result = model.getResult().getValue();
            JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get())
                    .orElseThrow(() -> new IllegalStateException("Token must not be null at this stage."));
            Async.execute(() -> {
                try {
                    final String member = token.getClaim("member").asString();
                    HLFMiddlewareAPIClient.getInstance().changeDoc(ChangeDocRequest.builder()
                            .documentId(result.getCardSwiped().getDocument().getDocumentId())
                            .member(member)
                            .type(result.approved() ? "APPROVE" : "REJECT")
                            .details(result.rejected() ? ((Result.Reject) result).getReason() : null)
                            .build(), token.toString());
                    needUpdate = true;
                    requireActivity().runOnUiThread(() ->
                            showUserMessage(String.format(getString(
                                    result.approved()
                                            ? R.string.doc_approved_hint
                                            : R.string.doc_rejected_hint), member)));
                } catch (HLFException e) {
                    cardStackView.rewind();
                    requireActivity().runOnUiThread(() -> showUserMessage(R.string.unexpected_error));
                }
            });
            // show hint if it was the last card swiped
            if (manager.getTopPosition() == adapter.getItemCount()) {
                root.findViewById(R.id.noMoreDocsHint).setVisibility(View.VISIBLE);
            }
        });
        manager = new CardStackLayoutManager(requireContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
            }

            @Override
            public void onCardSwiped(Direction direction) {
                final SwipeItemModel docSwiped = adapter.getItems().get(manager.getTopPosition() - 1);
                if (direction == Direction.Right) {
                    processApprove(docSwiped);
                } else if (direction == Direction.Left) {
                    processReject(docSwiped);
                } else if (direction == Direction.Bottom) {
                    processEdit(docSwiped);
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {
                // can't move this logic to obBind holder as binding triggers for all holder,
                // should manage manager state dynamically
                final Document document = adapter.getItems().get(position).getDocument();
                // handle its status
                if ("REJECTED".equals(document.getStatus())) {
                    manager.setDirections(Direction.VERTICAL);
                    manager.setCanScrollHorizontal(false);
                    manager.setCanScrollVertical(true);
                } else {
                    manager.setDirections(Direction.HORIZONTAL);
                    manager.setCanScrollVertical(false);
                    manager.setCanScrollHorizontal(true);
                }
            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });
        manager.setStackFrom(StackFrom.Left);
        manager.setVisibleCount(5);
        manager.setTranslationInterval(20.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.4f);
        manager.setMaxDegree(200.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollVertical(false);
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        manager.setRewindAnimationSetting(new RewindAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new DecelerateInterpolator())
                .build());
        manager.setOverlayInterpolator(new OvershootInterpolator(4.f));
        adapter = new DocStackAdapter(Collections.emptyList());
        cardStackView.setAdapter(adapter);
        // docs binding
        MainActivity.getModel().getDocsResult().observe(getViewLifecycleOwner(), getDocsResult -> {
            if (Objects.nonNull(getDocsResult.getDocuments())) {
                adapter.setItems(
                        filterDocs(Objects.requireNonNull(getDocsResult.getDocuments())).stream()
                                .map(SwipeItemModel::new)
                                .collect(Collectors.toList())
                );
                if (adapter.getItemCount() == 0) {
                    root.findViewById(R.id.noMoreDocsHint).setVisibility(View.VISIBLE);
                }
            } else if (Objects.nonNull(getDocsResult.getError())) {
                showUserMessage(getDocsResult.getError());
            }
        });
        cardStackView.setLayoutManager(manager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        return root;
    }

    private void processEdit(SwipeItemModel docSwiped) {
        Intent intent = new Intent(requireContext(), CreateDocumentWizardActivity.class);
        intent.putExtra("edit", docSwiped.getDocument());
        ArrayList<String> arr = new ArrayList<>();
        arr.add("Другое");
        intent.putStringArrayListExtra("docTypes", arr);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String message = Optional.ofNullable(data)
                        .map(self -> self.getStringExtra("answer"))
                        .orElse(null);
                trace("Answer: " + (StringUtils.isNotEmpty(message) ? message : "Didn't got! rewind!!"));
                if (StringUtils.isEmpty(message)) {
                    cardStackView.rewind();
                }
            }
        }
    }
    @Override
    public void onDestroyView() {
        if (needUpdate)
            Async.execute(() -> MainActivity.getModel().getDocuments());
        super.onDestroyView();
    }

    private void processApprove(SwipeItemModel cardSwiped) {
        // call fabric sign doc by user
        model.getResult().setValue(new Result.Approve(cardSwiped));
    }

    private List<Document> filterDocs(List<Document> documents) {
        final JWT token = SessionManager.getInstance().getUserToken(requireContext())
                .orElseThrow(() -> new IllegalStateException("Token must not be null at this stage."));
        final String member = Objects.requireNonNull(token.getClaim("member")).asString();
        // owner is not user logged or logged and doc is rejected and doc requires his sign and it is hit turn to sign\edit
        return documents.stream()
                // to approve/reject others users doc
                .filter(document -> (!member.equals(document.getOwner())
                        && document.getStatus().equals("PROCESSING")
                        && document.getSignsRequired().contains(member)
                        && member.equals(document.getCurrentSign()))
                        // to edit logged user rejected docs
                        || member.equals(document.getOwner()) && document.getStatus().equals("REJECTED"))
                .collect(Collectors.toList());
    }

    private EditText buildReasonForRejectField() {
        // input field configuring
        final EditText input = new EditText(requireContext());
        input.setSingleLine(false);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        input.setMinLines(3);
        input.setMaxLines(5);
        // restrict comment length to 120 characters
        InputFilter[] editFilters = input.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.LengthFilter(120);
        input.setFilters(newFilters);
        input.setVerticalScrollBarEnabled(true);
        input.setMovementMethod(ScrollingMovementMethod.getInstance());
        input.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        return input;
    }

    private void processReject(SwipeItemModel cardSwiped) {
        final EditText input = buildReasonForRejectField();
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.reason_for_reject)
                .setView(input)
                .setPositiveButton(R.string.submit_reject, (dialog, ignored) -> model.getResult().setValue(new Result.Reject(cardSwiped, input.getText().toString())))
                .setNegativeButton(R.string.cancel, (dialog, ignored) -> {
                    cardStackView.rewind();
                    dialog.cancel();
                }).show().setCanceledOnTouchOutside(false);
    }

}