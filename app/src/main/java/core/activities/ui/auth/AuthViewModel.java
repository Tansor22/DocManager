package core.activities.ui.auth;

import android.util.Patterns;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import core.activities.R;
import core.activities.data.AuthRepository;
import core.activities.data.Result;
import core.activities.data.model.LoggedInUser;
import core.activities.ui.shared.Async;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthViewModel extends ViewModel {
    @Getter
    MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    @Getter
    MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    AuthRepository authRepository;

    AuthViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    private void auth(Supplier<Result> auth) {
        Async.execute(() -> {
            Result result = auth.get();
            LoginResult lr;
            if (result instanceof Result.Success) {
                LoggedInUser data = ((Result.Success) result).getUser();
                lr = new LoginResult(new LoggedInUserView(data.getData().getMember(), data.getJwt()));
            } else {
                lr = new LoginResult(R.string.login_failed);
            }
            loginResult.postValue(lr);
        });
    }

    public void signIn(String username, String password) {
        auth(() -> authRepository.signIn(username, password));
    }

    public void signUp(String username, String password) {
        auth(() -> authRepository.signUp(username, password));
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 4;
    }
}