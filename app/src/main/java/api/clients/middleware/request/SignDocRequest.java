package api.clients.middleware.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SignDocRequest {
    String documentId;
    List<String> signs;
}
