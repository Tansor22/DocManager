package api.clients.middleware;

import android.content.res.Resources;
import api.clients.UtilsTLS;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.request.*;
import api.clients.middleware.response.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import okhttp3.*;
import okio.Buffer;

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

    private static HLFMiddlewareAPIClient INSTANCE;

    public static HLFMiddlewareAPIClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HLFMiddlewareAPIClient();
        }
        return INSTANCE;
    }

    @SneakyThrows(IOException.class)
    private HLFMiddlewareAPIClient() {
        final Resources resources = ApplicationContext.get().getResources();
        // todo change to android strings, retrieving middleware url from application properties
        String propertiesFileName = "application.properties";
        final InputStream inputStream = resources.getAssets().open(propertiesFileName);
        final Properties properties = new Properties();
        properties.load(inputStream);
        middlewareUrl = properties.getProperty("middleware.url");
        trace("Got middlewareUrl = %s from %s properties filename.", middlewareUrl, propertiesFileName);
        client = UtilsTLS.getSSLClient(resources);
    }

    public NewDocResponse newDoc(NewDocRequest request) throws HLFException {
        return executeRequest(request, NEW_DOC, NewDocResponse.class);
    }

    public GetDocsResponse getDocs(GetDocsRequest request) throws HLFException {

        return executeRequest(request, GET_DOCS, GetDocsResponse.class);
    }

    public SignDocResponse signDoc(SignDocRequest request) throws HLFException {
        return executeRequest(request, SIGN_DOC, SignDocResponse.class);
    }

    public SignUpResponse signUp(SignUpRequest request) throws HLFException {
        return executeRequest(request, SIGN_UP, SignUpResponse.class);
    }

    public SignInResponse signIn(SignInRequest request) throws HLFException {
        return executeRequest(request, SIGN_IN, SignInResponse.class);
    }


    @SneakyThrows(IOException.class)
    private <T> T executeRequest(Object request, HLFMiddlewareEndpoints endpoint, Class<T> responseEntity) throws HLFException {
        RequestBody body = RequestBody.create(JSON, jsonMapper.writeValueAsString(request));
        Request httpsRequest = new Request.Builder()
                .url(endpoint.getUrlForEndpoint(middlewareUrl))
                .post(body)
                .build();
        trace("Network request  %s\n" +
                "Body", httpsRequest.url(), bodyToString(httpsRequest));
        try (Response response = client.newCall(httpsRequest).execute()) {
            if (response.body() == null) {
                throw new IOException("No response body got from " + endpoint.getEndpointPath());
            }
            final JsonNode json = jsonMapper.readTree(response.body().byteStream());
            final String payload = json.get("payload").toString();
            if (response.code() != 200) {
                error("Error response %s", payload);
                final ErrorResponse errorResponse = jsonMapper.readValue(payload, ErrorResponse.class);
                throw HLFException.of(errorResponse);
            }
            trace("Response %s", payload);
            return jsonMapper.readValue(payload, responseEntity);
        }
    }

    private String bodyToString(final Request request) throws IOException {
        final Request copy = request.newBuilder().build();
        final Buffer buffer = new Buffer();
        if (copy.body() == null) {
            return "Empty body";
        }
        copy.body().writeTo(buffer);
        return buffer.readUtf8();
    }
}
