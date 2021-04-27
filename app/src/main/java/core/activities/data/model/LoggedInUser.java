package core.activities.data.model;

import api.clients.middleware.response.SignInResponse;
import com.auth0.android.jwt.JWT;
import core.sessions.SessionManager;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoggedInUser {
    SignInResponse data;
    JWT jwt;
    public static LoggedInUser fromSignInResponse(SignInResponse response) {
        JWT jwt = new JWT(response.getAccessToken());
        return new LoggedInUser(response, jwt);
    }
}