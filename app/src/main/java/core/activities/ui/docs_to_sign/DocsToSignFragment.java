package core.activities.ui.docs_to_sign;

import android.app.AlertDialog;
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
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import api.clients.middleware.exception.HLFException;
import com.yuyakaido.android.cardstackview.*;
import core.activities.R;
import core.activities.ui.docs_to_sign.model.Result;
import core.activities.ui.docs_to_sign.model.SignDocModel;
import core.activities.ui.docs_to_sign.swipe.DocStackAdapter;
import core.activities.ui.docs_to_sign.swipe.DocStackListener;
import core.activities.ui.docs_to_sign.swipe.SwipeItemModel;
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.UserMessageShower;
import core.shared.Traceable;

import java.util.Objects;
import java.util.stream.Collectors;

public class DocsToSignFragment extends Fragment implements Traceable, UserMessageShower {
    private CardStackLayoutManager manager;
    private DocStackAdapter adapter;
    private CardStackView cardStackView;
    private SignDocModel model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_docs_to_sign, container, false);
        cardStackView = root.findViewById(R.id.cardStackView);
        model = new ViewModelProvider(this).get(SignDocModel.class);
        model.getResult().observe(getViewLifecycleOwner(), self -> {
            // todo rename to processDoc
            try {
                model.processDoc();
            } catch (HLFException e) {
                cardStackView.rewind();
                showUserMessage(R.string.unexpected_error);
            }
            // show hint if it was the last card swiped
            if (manager.getTopPosition() == adapter.getItemCount()) {
                root.findViewById(R.id.noMoreDocsHint).setVisibility(View.VISIBLE);
            }
        });
        manager = new CardStackLayoutManager(requireContext(), (DocStackListener) direction -> {
            final SwipeItemModel docSwiped = adapter.getItems().get(manager.getTopPosition() - 1);
            if (direction == Direction.Right) {
                processApprove(docSwiped);
            }
            if (direction == Direction.Left) {
                processReject(docSwiped);
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
        // docs binding
        ((MainActivity) requireActivity()).getModel().getDocsResult().observe(getViewLifecycleOwner(), getDocsResult -> {
            if (Objects.nonNull(getDocsResult.getDocuments())) {
                // todo filter docs bu signs required by user logged in
                adapter = new DocStackAdapter(
                        Objects.requireNonNull(getDocsResult.getDocuments()).stream()
                        .map(SwipeItemModel::new)
                        .collect(Collectors.toList())
                );
                cardStackView.setAdapter(adapter);
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

    private void processApprove(SwipeItemModel cardSwiped) {
        // call fabric sign doc by user
        model.getResult().setValue(new Result.Approve(cardSwiped));
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