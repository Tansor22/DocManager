package api.clients.middleware.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetDocsRequest {
    String group;
}
