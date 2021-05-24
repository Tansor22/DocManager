package core.activities.ui.shared.forms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shamweel.jsontoforms.models.JSONModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class JSONModelEx extends JSONModel {

    // for TYPE_DATA_SUPPLIER
    @SerializedName("form")
    @Expose
    private List<JSONModel> form;

    @SerializedName("model")
    @Expose
    private Map<String, String> model;

    public static JSONModel picturedTextView(String id, String text) {
        final JSONModelEx output = new JSONModelEx();
        output.setId(id);
        output.setType(FormAdapterEx.TYPE_PICTURED_TEXT_VIEW);
        output.setText(text);
        return output;
    }
}
