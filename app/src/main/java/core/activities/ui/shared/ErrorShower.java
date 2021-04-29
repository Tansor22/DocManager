package core.activities.ui.shared;

import android.widget.Toast;
import androidx.annotation.StringRes;
import core.shared.ApplicationContext;

public interface ErrorShower {
    default void showError(@StringRes Integer errorRes) {
        Toast.makeText(ApplicationContext.get(), errorRes, Toast.LENGTH_SHORT).show();
    }

    default void showError(CharSequence errorString) {
        Toast.makeText(ApplicationContext.get(), errorString, Toast.LENGTH_SHORT).show();
    }
}
