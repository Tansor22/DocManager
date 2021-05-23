package api.clients.middleware.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeDocRequest {
    String documentId;
    String member;
    String type;
    String details;
}
