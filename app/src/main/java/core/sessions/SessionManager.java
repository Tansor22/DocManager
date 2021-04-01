package core.sessions;

import android.content.Context;
import lombok.val;

import java.util.Calendar;
import java.util.Date;

import static core.sessions.SessionConstants.*;

public class SessionManager {
    public static void startUserSession(Context context, int expiresIn) {
        val calendar = Calendar.getInstance();
        val userLoggedInTime = calendar.getTime();
        calendar.setTime(userLoggedInTime);
        calendar.add(Calendar.SECOND, expiresIn);
        val expiryTime = calendar.getTime();
        val tokenSharedPreferences = context.getSharedPreferences(SESSION_PREFERENCES, 0);
        val editor = tokenSharedPreferences.edit();
        editor.putLong(SESSION_EXPIRY_TIME, expiryTime.getTime());
        editor.apply();
    }

    public static boolean isSessionActive(Date currentTime, Context context) {
        val sessionExpiresAt = new Date(getExpiryDateFromPreferences(context));
        return !currentTime.after(sessionExpiresAt);
    }

    private static long getExpiryDateFromPreferences(Context context) {
        return context.getSharedPreferences(SESSION_PREFERENCES, 0).getLong(SESSION_EXPIRY_TIME, 0);
    }

    public static void storeUserToken(Context context, String token) {
        store(context, SESSION_TOKEN, token);
    }

    public static void store(Context context, String key, String value) {
        val tokenEditor = context.getSharedPreferences(SESSION_PREFERENCES, 0).edit();
        tokenEditor.putString(key, value);
        tokenEditor.apply();
    }

    public static String get(Context context, String key) {
        return context.getSharedPreferences(SESSION_PREFERENCES, 0).getString(key, "");
    }

    public static String getUserToken(Context context) {
        return get(context, SESSION_TOKEN);
    }

    public void endUserSession(Context context) {
        clearStoredData(context);
    }

    private static void clearStoredData(Context context) {
        val editor = context.getSharedPreferences(SESSION_PREFERENCES, 0).edit();
        editor.clear();
        editor.apply();
    }
}