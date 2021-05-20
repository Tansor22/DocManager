package api.clients.middleware;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public enum HLFMiddlewareEndpoints {
    SIGN_UP("/api/auth/signUp"),
    SIGN_IN("/api/auth/signIn"),
    // user apis
    NEW_DOC("/api/chaincode/newDoc"),
    CHANGE_DOC("/api/chaincode/changeDoc"),
    GET_DOCS("/api/chaincode/getDocs");
    String endpointPath;

    public String getUrlForEndpoint(String url) {
        return url + endpointPath;
    }
}
