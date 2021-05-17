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
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import api.clients.middleware.entity.Document;
import com.yuyakaido.android.cardstackview.*;
import core.activities.R;
import core.activities.ui.doc_details.model.SignDocModel;
import core.activities.ui.doc_details.model.SignDocResult;
import core.activities.ui.doc_details.swipe.CardStackAdapter;
import core.activities.ui.doc_details.swipe.SwipeItemModel;
import core.shared.Traceable;

import java.util.ArrayList;
import java.util.List;

public class DocDetailsActivity extends AppCompatActivity implements Traceable {
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private CardStackView cardStackView;
    private SignDocModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Document doc = getIntent().getParcelableExtra("doc");
        trace("Doc received = %s", doc.toString());
        setContentView(R.layout.activity_doc_details);
        cardStackView = findViewById(R.id.cardStackView);
        model = new ViewModelProvider(this).get(SignDocModel.class);
        model.getResult().observe(this, self -> {
            trace(self.getApprove() != null ? self.getApprove().getClass().getSimpleName() : self.getReject().getClass().getSimpleName());
            if (self.getReject() != null) {
                trace("Reason " + self.getReject().getReason());
            }
            // show hint if it was the last card swiped
            if (manager.getTopPosition() == adapter.getItemCount()) {
                findViewById(R.id.noMoreDocsHint).setVisibility(View.VISIBLE);
            }
        });
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //trace("onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                trace("onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right) {
                    processApprove();
                }
                if (direction == Direction.Left) {
                    processReject();
                }
            }

            @Override
            public void onCardRewound() {
                trace("onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                trace("onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                trace("onCardAppeared: " + position + ", nama: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                trace("onCardDisAppeared: " + position + ", nama: " + tv.getText());
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
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void processApprove() {
        // call fabric sign doc by user
        model.getResult().setValue(new SignDocResult(new SignDocResult.Approve()));
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

    private void processReject() {
        final EditText input = buildReasonForRejectField();
        new AlertDialog.Builder(this)
                .setTitle(R.string.reason_for_reject)
                .setView(input)
                .setPositiveButton(R.string.submit_reject, (dialog, ignored) -> {
                    // call fabric reject doc ???
                    model.getResult().setValue(new SignDocResult(new SignDocResult.Reject(input.getText().toString())));
                    trace("Comment: %s", input.getText().toString());
                })
                .setNegativeButton(R.string.cancel, (dialog, ignored) -> {
                    cardStackView.rewind();
                    dialog.cancel();
                }).show();
    }

    private List<SwipeItemModel> addList() {
        List<SwipeItemModel> items = new ArrayList<>();
        items.add(new SwipeItemModel(R.drawable.sample1, "Markonah", "24", "Jonggfddfdfdfdfdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddol"));
        items.add(new SwipeItemModel(R.drawable.sample2, "Marpuah", "20", "Malang"));
        items.add(new SwipeItemModel(R.drawable.sample3, "Sukijah", "27", "Jonggfddddddddddddddddddddddddddddddddddddddddddddddol"));
        items.add(new SwipeItemModel(R.drawable.sample4, "Markobar", "19", "Bandung"));
        items.add(new SwipeItemModel(R.drawable.sample5, "Marmut", "25", "Hutan"));

        items.add(new SwipeItemModel(R.drawable.sample1, "Markonah", "24", "Jember"));
        items.add(new SwipeItemModel(R.drawable.sample2, "Marpuah", "20", "Malang"));
        items.add(new SwipeItemModel(R.drawable.sample3, "Sukijah", "27", "Jonggol"));
        items.add(new SwipeItemModel(R.drawable.sample4, "Markobar", "19", "Bandung"));
        items.add(new SwipeItemModel(R.drawable.sample5, "Marmut", "25", "Hutan"));
        return items;
    }
}