package core.activities.ui.docs_to_sign.model;

import core.activities.ui.docs_to_sign.swipe.SwipeItemModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
public abstract class Result {
    SwipeItemModel cardSwiped;

    public abstract boolean approved();

    public abstract boolean rejected();

    @EqualsAndHashCode(callSuper = true)
    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class Reject extends Result {
        String reason;

        public Reject(SwipeItemModel cardSwiped, String reason) {
            super(cardSwiped);
            this.reason = reason;
        }

        @Override
        public boolean approved() {
            return false;
        }

        @Override
        public boolean rejected() {
            return true;
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Approve extends Result {
        public Approve(SwipeItemModel cardSwiped) {
            super(cardSwiped);
        }

        @Override
        public boolean approved() {
            return true;
        }

        @Override
        public boolean rejected() {
            return false;
        }
    }
}
