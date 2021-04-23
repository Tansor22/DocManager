package core.activities.data;

import core.activities.data.model.LoggedInUser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthRepository {

    static volatile AuthRepository INSTANCE;

    AuthDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    @NonFinal
    LoggedInUser user;

    public static AuthRepository getInstance(AuthDataSource dataSource) {
        if (INSTANCE == null) {
            INSTANCE = new AuthRepository(dataSource, null);
        }
        return INSTANCE;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result signIn(String username, String password) {
        Result result = dataSource.signIn(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success) result).getUser());
        }
        return result;
    }

    public Result signUp(String username, String password) {
        Result result = dataSource.signUp(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success) result).getUser());
        }
        return result;
    }
}