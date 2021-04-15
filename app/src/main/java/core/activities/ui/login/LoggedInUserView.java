package core.activities.ui.login;

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
    //... other data fields that may be accessible to the UI
}