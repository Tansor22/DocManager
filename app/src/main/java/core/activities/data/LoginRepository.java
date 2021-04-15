package core.activities.data;

import core.activities.data.model.LoggedInUser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
// todo пробросить middleware клиента
public class LoginRepository {

    static volatile LoginRepository INSTANCE;

    LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    @NonFinal
    LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (INSTANCE == null) {
            INSTANCE = new LoginRepository(dataSource);
        }
        return INSTANCE;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result login(String username, String password) {
        // handle login
        Result result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success) result).getUser());
        }
        return result;
    }
}