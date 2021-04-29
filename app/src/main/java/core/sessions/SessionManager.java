package core.sessions;

import android.content.Context;
import com.auth0.android.jwt.JWT;
import core.shared.Traceable;
import lombok.val;

import java.util.Objects;
import java.util.Optional;

import static core.sessions.SessionConstants.SESSION_PREFERENCES;
import static core.sessions.SessionConstants.SESSION_TOKEN;

public class SessionManager implements Traceable {
    private static SessionManager INSTANCE;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return Objects.nonNull(INSTANCE) ? INSTANCE : (INSTANCE = new SessionManager());
    }

    public void startUserSession(Context context, JWT token) {
        val tokenSharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, 0);
        val editor = tokenSharedPreferences.edit();
        trace("Started user session with token %s, expires at %s", token.toString(), token.getExpiresAt());
        editor.putString(SESSION_TOKEN, token.toString());
        editor.apply();
    }

    public void store(Context context, String key, String value) {
        val tokenEditor = context.getSharedPreferences(SESSION_PREFERENCES, 0).edit();
        tokenEditor.putString(key, value);
        tokenEditor.apply();
    }

    public String get(Context context, String key) {
        return context.getSharedPreferences(SESSION_PREFERENCES, 0).getString(key, null);
    }

    public Optional<JWT> getUserToken(Context context) {
        final String token = get(context, SESSION_TOKEN);
        return Optional.ofNullable(token)
                .map(JWT::new);
    }

    public void endUserSession(Context context) {
        clearStoredData(context);
    }

    private void clearStoredData(Context context) {
        val sharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, 0);
        trace("End session with token %s", sharedPreferences.getString(SESSION_TOKEN, null));
        val editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}