package core.activities.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import api.clients.middleware.HLFMiddlewareAPIClient;
import core.activities.data.LoginDataSource;
import core.activities.data.LoginRepository;
import lombok.AllArgsConstructor;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
@AllArgsConstructor
public class LoginViewModelFactory implements ViewModelProvider.Factory {
    HLFMiddlewareAPIClient apiClient;

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(LoginRepository.getInstance(new LoginDataSource(apiClient)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}