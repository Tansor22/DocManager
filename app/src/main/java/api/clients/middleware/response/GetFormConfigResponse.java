package api.clients.middleware.response;

import core.activities.ui.shared.forms.JSONModelEx;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetFormConfigResponse {
    List<JSONModelEx> config;
}
