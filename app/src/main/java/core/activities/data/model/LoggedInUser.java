package core.activities.data.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Data
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LoggedInUser {
    String userId;
    String displayName;
}