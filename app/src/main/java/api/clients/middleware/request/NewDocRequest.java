package api.clients.middleware.request;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Builder
@Data
public class NewDocRequest {
    String title;
    String type;
    String owner;
    String group;
    String content;
    @Singular(value = "signRequired")
    List<String> signsRequired;
}
