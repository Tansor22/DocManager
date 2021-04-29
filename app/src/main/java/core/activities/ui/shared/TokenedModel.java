package core.activities.ui.shared;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.auth0.android.jwt.JWT;
import core.sessions.SessionManager;

public abstract class TokenedModel extends AndroidViewModel {
    public JWT token;

    public TokenedModel(@NonNull Application application) {
        super(application);

        final String userToken = SessionManager.getUserToken(application);
        token = new JWT(userToken);
    }

}
