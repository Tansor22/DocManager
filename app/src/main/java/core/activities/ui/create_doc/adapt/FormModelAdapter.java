package core.activities.ui.create_doc.adapt;

import api.clients.middleware.entity.Document;
import com.shamweel.jsontoforms.models.JSONModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FormModelAdapter {
    public static <T extends JSONModel> void adapt(List<T> formModel, Document document) {

    }

}
