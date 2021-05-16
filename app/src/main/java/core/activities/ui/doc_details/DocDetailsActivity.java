package core.activities.ui.doc_details;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import api.clients.middleware.entity.Document;
import com.yuyakaido.android.cardstackview.*;
import core.activities.R;
import core.activities.ui.doc_details.swipe.CardStackAdapter;
import core.activities.ui.doc_details.swipe.CardStackCallback;
import core.activities.ui.doc_details.swipe.SwipeItemModel;
import core.shared.Traceable;

import java.util.ArrayList;
import java.util.List;

public class DocDetailsActivity extends AppCompatActivity implements Traceable {
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Document doc = getIntent().getParcelableExtra("doc");
        trace("Doc received = %s", doc.toString());
        setContentView(R.layout.activity_doc_details);
        final CardStackView cardStackView = findViewById(R.id.cardStackView);
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //trace("onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                trace("onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                if (direction == Direction.Right){
                    Toast.makeText(DocDetailsActivity.this, "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top){
                    Toast.makeText(DocDetailsActivity.this, "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left){
                    Toast.makeText(DocDetailsActivity.this, "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    Toast.makeText(DocDetailsActivity.this, "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                // Paginating
                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    paginate();
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
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollVertical(false);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    private void paginate() {
        List<SwipeItemModel> old = adapter.getItems();
        List<SwipeItemModel> baru = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, baru);
        DiffUtil.DiffResult hasil = DiffUtil.calculateDiff(callback);
        adapter.setItems(baru);
        hasil.dispatchUpdatesTo(adapter);
    }


    private List<SwipeItemModel> addList() {
        List<SwipeItemModel> items = new ArrayList<>();
        items.add(new SwipeItemModel(R.drawable.sample1, "Markonah", "24", "Jember"));
        items.add(new SwipeItemModel(R.drawable.sample2, "Marpuah", "20", "Malang"));
        items.add(new SwipeItemModel(R.drawable.sample3, "Sukijah", "27", "Jonggol"));
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