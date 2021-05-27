package core.activities.ui.create_doc.adapt;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.Document;
import com.shamweel.jsontoforms.models.JSONModel;

import java.util.List;

public class GeneralFormModelAdapter extends FormModelAdapter<Attributes> {
    public GeneralFormModelAdapter(Document document) {
        super(document);
    }

    @Override
    protected <T extends JSONModel> void adapt(List<T> formModel, Attributes attrs) {
        for (T elementModel : formModel) {
            if ("content".equals(elementModel.getId())) {
                elementModel.setText(attrs.getContent());
            } else if ("title".equals(elementModel.getId())) {
                // todo should adapt general signs at least
                elementModel.setText(document.getTitle());
            } else if ("signs".equals(elementModel.getId())) {
                elementModel.setText(String.join(", ", document.getSignsRequired()));
            }
        }
    }
}
