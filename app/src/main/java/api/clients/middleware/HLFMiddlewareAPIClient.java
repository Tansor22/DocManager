package api.clients.middleware;

import android.content.res.Resources;
import core.activities.R;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static api.clients.middleware.HLFMiddlewareEndpoints.ACCESS_HLF;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HLFMiddlewareAPIClient implements Traceable {
    String middlewareUrl;
    OkHttpClient client;


    @SneakyThrows(IOException.class)
    public HLFMiddlewareAPIClient(Resources resources) {
        // retrieving middleware url from application properties
        String propertiesFileName = "application.properties";
        final InputStream inputStream = resources.getAssets().open(propertiesFileName);
        final Properties properties = new Properties();
        properties.load(inputStream);
        middlewareUrl = properties.getProperty("middleware.url");
        trace("Got middlewareUrl = %s from %s properties filename.", middlewareUrl, propertiesFileName);
        // client can be configured additionally
        client = new OkHttpClient();
    }

    @SneakyThrows(IOException.class)
    public String accessHLF() {
        Request request = new Request.Builder()
                .url(ACCESS_HLF.getUrlForEndpoint(middlewareUrl))
                .build();
        Response response = client.newCall(request).execute();
        if (response.body() == null) {
            throw new IOException("No response body got from " + ACCESS_HLF.getEndpointPath());
        } else {
            return response.body().string();
        }
    }
}
