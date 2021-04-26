package api.clients.middleware.exception;

import api.clients.middleware.response.ErrorResponse;

public class HLFException extends Exception {

    private HLFException(String code, String details) {
        super(String.format("%s: %s", code, details));
    }

    // hierarchy can be extended to capture different types of ex in client code
    public static HLFException of(ErrorResponse errorResponse) {
        return new HLFException(errorResponse.getError(), errorResponse.getDetails());
    }
}
