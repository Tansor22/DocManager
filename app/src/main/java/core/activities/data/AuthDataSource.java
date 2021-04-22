package core.activities.data;

import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.request.SignInRequest;
import api.clients.middleware.response.SignInResponse;
import core.activities.data.model.LoggedInUser;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
@AllArgsConstructor
public class AuthDataSource {

    public Result login(String username, String password) {
        try {
            final SignInRequest signInRequest = new SignInRequest(username, password);
            final SignInResponse signInResponse = HLFMiddlewareAPIClient.getInstance().signIn(signInRequest);
            LoggedInUser user = new LoggedInUser(signInResponse);
            return new Result.Success(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}