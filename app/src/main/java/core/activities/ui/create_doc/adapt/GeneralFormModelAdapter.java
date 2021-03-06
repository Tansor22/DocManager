package core.activities.ui.create_doc.adapt;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.Document;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;

public class GeneralFormModelAdapter extends FormModelAdapter<Attributes> {
    public GeneralFormModelAdapter(Document document) {
        super(document);
    }

    @Override
    protected <T extends JSONModel> void adaptInternal(T model, Attributes attrs) {
        if ("content".equals(model.getId())) {
            DataValueHashMap.put("content", attrs.getContent());
        }
    }
}
