package core.activities.ui.login;

import androidx.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

/**
 * Data validation state of the login form.
 */
@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
class LoginFormState {
    @Nullable
    Integer usernameError;
    @Nullable
    Integer passwordError;
    boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }
}