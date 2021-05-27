package api.clients.middleware;

import android.content.res.Resources;
import api.clients.UtilsTLS;
import api.clients.middleware.entity.Document;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.json.DocumentAdapter;
import api.clients.middleware.request.*;
import api.clients.middleware.response.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import static api.clients.middleware.HLFMiddlewareEndpoints.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HLFMiddlewareAPIClient implements Traceable {
    String middlewareUrl;
    OkHttpClient client;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Document.class, new DocumentAdapter())
            //.registerTypeAdapter(NewDocRequest.class, new NewDocRequestAdapter())
            .disableHtmlEscaping()
            .create();
    static MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    static String X_ACCESS_TOKEN_HEADER = "x-access-token";


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

    public NewDocResponse newDoc(NewDocRequest request, String token) throws HLFException {
        return executeRequest(prepareAndLogRequest(request, NEW_DOC,
                Headers.of(X_ACCESS_TOKEN_HEADER, token)), NewDocResponse.class);
    }

    public GetDocsResponse getDocs(GetDocsRequest request, String token) throws HLFException {
        return executeRequest(prepareAndLogRequest(request, GET_DOCS,
                Headers.of(X_ACCESS_TOKEN_HEADER, token)), GetDocsResponse.class);
    }

    public ChangeDocResponse changeDoc(ChangeDocRequest request, String token) throws HLFException {
        return executeRequest(prepareAndLogRequest(request, CHANGE_DOC,
                Headers.of(X_ACCESS_TOKEN_HEADER, token)), ChangeDocResponse.class);
    }

    public GetFormConfigResponse getFormConfig(GetFormConfigRequest request, String token) throws HLFException {
        return executeRequest(prepareAndLogRequest(request, GET_FORM_CONFIG,
                Headers.of(X_ACCESS_TOKEN_HEADER, token)), GetFormConfigResponse.class);
    }

    public SignUpResponse signUp(SignUpRequest request) throws HLFException {
        return executeRequest(prepareAndLogRequest(request, SIGN_UP), SignUpResponse.class);
    }

    public SignInResponse signIn(SignInRequest request) throws HLFException {
        return executeRequest(prepareAndLogRequest(request, SIGN_IN), SignInResponse.class);
    }


    private Request prepareAndLogRequest(Object request, HLFMiddlewareEndpoints endpoint) {
        return prepareAndLogRequest(request, endpoint, null);
    }

    @SneakyThrows(IOException.class)
    private Request prepareAndLogRequest(Object request, HLFMiddlewareEndpoints endpoint, Headers headers) {
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, gson.toJson(request));
        Request.Builder httpsRequestBuilder = new Request.Builder()
                .url(endpoint.getUrlForEndpoint(middlewareUrl))
                .post(body);
        if (Objects.nonNull(headers)) {
            httpsRequestBuilder.headers(headers);
        }
        Request httpsRequest = httpsRequestBuilder.build();
        trace("Network request  %s\n" +
                "Body", httpsRequest.url(), bodyToString(httpsRequest));
        return httpsRequest;
    }

    @SneakyThrows(IOException.class)
    private <T> T executeRequest(Request request, Class<T> responseEntity) throws HLFException {
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IOException("No response body got from " + request.url());
            }
            final JsonObject json = gson.fromJson(response.body().charStream(), JsonObject.class);
            final JsonObject payload = json.getAsJsonObject("payload");
            if (response.code() != 200) {
                error("Error response %s", payload);
                final ErrorResponse errorResponse = gson.fromJson(payload, ErrorResponse.class);
                throw HLFException.of(errorResponse);
            }
            trace("Response %s", payload);
            return gson.fromJson(payload, responseEntity);
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
