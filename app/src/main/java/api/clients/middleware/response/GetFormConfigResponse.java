package api.clients.middleware.response;

import com.shamweel.jsontoforms.models.JSONModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetFormConfigResponse {
    List<JSONModel> config;
}
