package api.clients.middleware;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public enum HLFMiddlewareEndpoints {
    ACCESS_HLF("/accessHLF")
    ;
    String endpointPath;

    public String getUrlForEndpoint(String url) {
        return url + endpointPath;
    }
}
