package api.clients.middleware.request;

import api.clients.middleware.entity.Attributes;
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
    Attributes attributes;
    @Singular(value = "signRequired")
    List<String> signsRequired;
}
