package core.shared;

import android.content.Context;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@UtilityClass
public class CommonUtils implements Traceable {

    @SneakyThrows(IOException.class)
    public static String loadFileContentFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            new CommonUtils().trace("Exception Occurred : " + ex.getMessage());
            throw ex;
        }
    }
}
