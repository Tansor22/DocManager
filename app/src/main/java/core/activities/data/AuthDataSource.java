package core.activities.data;

import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.request.SignInRequest;
import api.clients.middleware.request.SignUpRequest;
import api.clients.middleware.response.SignInResponse;
import api.clients.middleware.response.SignUpResponse;
import core.activities.data.model.LoggedInUser;
import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
@AllArgsConstructor
public class AuthDataSource {

    public Result signIn(String username, String password) {
        try {
            final SignInRequest signInRequest = new SignInRequest(username, password);
            final SignInResponse signInResponse = HLFMiddlewareAPIClient.getInstance().signIn(signInRequest);
            LoggedInUser user = LoggedInUser.fromSignInResponse(signInResponse);
            return new Result.Success(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }


    public Result signUp(String username, String password) {
        try {
            // todo сделать email
            final SignUpRequest signUpRequest = new SignUpRequest(username, username, password);
            final SignUpResponse signUpResponse = HLFMiddlewareAPIClient.getInstance().signUp(signUpRequest);
            if ("Ok".equals(signUpResponse.getResult())) {
               return signIn(username, password);
            } else {
                return new Result.Error(new IOException("Unexpected result got"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }
}