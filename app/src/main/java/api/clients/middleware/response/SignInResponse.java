package api.clients.middleware.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInResponse {
    String email;
    String username;
    String accessToken;
}
