package api.clients.middleware.response;

import lombok.Data;

@Data
public class ErrorResponse{
    String error;
    String details;
}
