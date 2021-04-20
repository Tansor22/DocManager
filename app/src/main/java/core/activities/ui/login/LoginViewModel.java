package core.activities.ui.login;

import android.util.Patterns;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import core.activities.R;
import core.activities.data.LoginRepository;
import core.activities.data.Result;
import core.activities.data.model.LoggedInUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoginViewModel extends ViewModel {
    @Getter
    MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    @Getter
    MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    LoginRepository loginRepository;
    ExecutorService executorService;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void login(String username, String password) {
        executorService.execute(() -> {
            Result result = loginRepository.login(username, password);
            LoginResult lr;
            if (result instanceof Result.Success) {
                LoggedInUser data = ((Result.Success) result).getUser();
                lr = new LoginResult(new LoggedInUserView(data.getSignInResponse().getUsername()));
            } else {
                lr = new LoginResult(R.string.login_failed);
            }
            loginResult.postValue(lr);
        });
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
        return password != null && password.trim().length() > 5;
    }
}