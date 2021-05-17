package core.activities.ui.doc_details;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import api.clients.middleware.entity.Document;
import api.clients.middleware.exception.HLFException;
import com.yuyakaido.android.cardstackview.*;
import core.activities.R;
import core.activities.ui.doc_details.model.SignDocModel;
import core.activities.ui.doc_details.model.Result;
import core.activities.ui.doc_details.swipe.DocStackAdapter;
import core.activities.ui.doc_details.swipe.DocStackListener;
import core.activities.ui.doc_details.swipe.SwipeItemModel;
import core.activities.ui.shared.UserMessageShower;
import core.shared.Traceable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DocDetailsActivity extends AppCompatActivity implements Traceable, UserMessageShower {
    private CardStackLayoutManager manager;
    private DocStackAdapter adapter;
    private CardStackView cardStackView;
    private SignDocModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final List<Document> docs = getIntent().getParcelableArrayListExtra("docs");
        final int position = getIntent().getIntExtra("position", 0);
        //trace("Docs received = %s", docs.toString());
        //trace("Position received = %s", position);
        setContentView(R.layout.activity_doc_details);
        cardStackView = findViewById(R.id.cardStackView);
        model = new ViewModelProvider(this).get(SignDocModel.class);
        model.getResult().observe(this, self -> {
            // todo rename to processDoc
            try {
                model.traceResult();
            } catch (HLFException e) {
                cardStackView.rewind();
                showUserMessage(R.string.unexpected_error);
            }
            // show hint if it was the last card swiped
            if (manager.getTopPosition() == adapter.getItemCount()) {
                findViewById(R.id.noMoreDocsHint).setVisibility(View.VISIBLE);
            }
        });
        manager = new CardStackLayoutManager(this, (DocStackListener) direction -> {
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
        adapter = new DocStackAdapter(docs.stream().map( doc ->
                new SwipeItemModel(R.drawable.doc_bg, doc.getTitle(), doc.getDate(), doc.getStatus())
        ).collect(Collectors.toList()));
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void processApprove(SwipeItemModel cardSwiped) {
        // call fabric sign doc by user
        model.getResult().setValue(new Result.Approve(cardSwiped));
    }

    private EditText buildReasonForRejectField() {
        // input field configuring
        final EditText input = new EditText(this);
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
        new AlertDialog.Builder(this)
                .setTitle(R.string.reason_for_reject)
                .setView(input)
                .setPositiveButton(R.string.submit_reject, (dialog, ignored) -> model.getResult().setValue(new Result.Reject(cardSwiped, input.getText().toString())))
                .setNegativeButton(R.string.cancel, (dialog, ignored) -> {
                    cardStackView.rewind();
                    dialog.cancel();
                }).show().setCanceledOnTouchOutside(false);
    }

    private List<SwipeItemModel> addList() {
        List<SwipeItemModel> items = new ArrayList<>();
        items.add(new SwipeItemModel(R.drawable.sample1, "Markonah", "24", "Jongdol"));
        items.add(new SwipeItemModel(R.drawable.sample2, "Marpuah", "20", "Malang"));
        items.add(new SwipeItemModel(R.drawable.sample3, "Sukijah", "27", "Jongdol"));
        items.add(new SwipeItemModel(R.drawable.sample4, "Markobar", "19", "Bandung"));
        items.add(new SwipeItemModel(R.drawable.sample5, "Marmut", "25", "Hutan"));
        return items;
    }
}