package core.shared;

import android.content.Context;
import android.util.Log;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class JsonUtils implements Tagged {

    private static final String TAG = "CommonUtils";

    @SneakyThrows(IOException.class)
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.d(TAG, "Exception Occurred : " + ex.getMessage());
            throw ex;
        }

    }
}
