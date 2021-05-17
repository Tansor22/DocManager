package core.activities.ui.doc_details.swipe;

import android.view.View;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;

@FunctionalInterface
public interface DocStackListener extends CardStackListener {

    @Override
    default void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    default void onCardRewound() {

    }

    @Override
    default void onCardCanceled() {

    }

    @Override
    default void onCardAppeared(View view, int position) {

    }

    @Override
    default void onCardDisappeared(View view, int position) {

    }
}
