package core.shared;

import android.util.Log;

import java.util.Objects;

public interface Traceable extends Tagged {
    default void trace(String message) {
        trace(message, new Object[]{});
    }

    default void trace(String message, Object... args) {
        if (Objects.isNull(message)) {
            throw new NullPointerException("Message cannot be null");
        }
        Log.i(getTag(), String.format(message, args));
    }
}
