package api.clients.middleware;

import android.content.res.Resources;
import api.clients.middleware.request.GetDocsRequest;
import api.clients.middleware.request.NewDocRequest;
import api.clients.middleware.request.SignDocRequest;
import api.clients.middleware.response.GetDocsResponse;
import api.clients.middleware.response.NewDocResponse;
import api.clients.middleware.response.SignDocResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static api.clients.middleware.HLFMiddlewareEndpoints.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HLFMiddlewareAPIClient implements Traceable {
    String middlewareUrl;
    OkHttpClient client;
    ObjectMapper jsonMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    static MediaType JSON = MediaType.parse("application/json; charset=utf-8");


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

    public NewDocResponse newDoc(NewDocRequest request) {
        return executeRequest(request, NEW_DOC, NewDocResponse.class);
    }

    public GetDocsResponse getDocs(GetDocsRequest request) {

        return executeRequest(request, GET_DOCS, GetDocsResponse.class);
    }

    public SignDocResponse signDoc(SignDocRequest request) {
        return executeRequest(request, SIGN_DOC, SignDocResponse.class);
    }


    @SneakyThrows(IOException.class)
    private <T> T executeRequest(Object request, HLFMiddlewareEndpoints endpoint, Class<T> responseEntity) {
        RequestBody body = RequestBody.create(JSON, jsonMapper.writeValueAsString(request));
        Request htpRequest = new Request.Builder()
                .url(endpoint.getUrlForEndpoint(middlewareUrl))
                .post(body)
                .build();
        try (Response response = client.newCall(htpRequest).execute()) {
            if (response.body() == null) {
                throw new IOException("No response body got from " + endpoint.getEndpointPath());
            }
            final JsonNode json = jsonMapper.readTree(response.body().byteStream());
            return jsonMapper.readValue(json.get("response").toString(), responseEntity);
        }
    }
}
