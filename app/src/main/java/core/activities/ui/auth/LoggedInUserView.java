package core.activities.ui.auth;

import com.auth0.android.jwt.JWT;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Class exposing authenticated user details to the UI.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
// session object
class LoggedInUserView {
    String displayName;
    JWT jwt;
}