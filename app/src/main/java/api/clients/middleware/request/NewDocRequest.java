package api.clients.middleware.request;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Builder
@Data
public class NewDocRequest {
    // todo develop
    String org;
    String content;
    @Singular(value = "signRequired")
    List<String> signsRequired;
}
