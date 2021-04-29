package core.activities.ui.shared;

import android.widget.Toast;
import androidx.annotation.StringRes;
import core.shared.ApplicationContext;

public interface UserMessageShower {
    default void showUserMessage(@StringRes Integer messageRes) {
        Toast.makeText(ApplicationContext.get(), messageRes, Toast.LENGTH_SHORT).show();
    }

    default void showUserMessage(CharSequence messageString) {
        Toast.makeText(ApplicationContext.get(), messageString, Toast.LENGTH_SHORT).show();
    }
}
