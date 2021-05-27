package core.activities.ui.create_doc.adapt;

import api.clients.middleware.entity.Document;
import api.clients.middleware.entity.GraduationThesisTopicsAttributes;
import com.shamweel.jsontoforms.models.JSONModel;

import java.util.List;

public class GraduationThesisTopicsFormModelAdapter extends FormModelAdapter<GraduationThesisTopicsAttributes> {
    public GraduationThesisTopicsFormModelAdapter(Document document) {
        super(document);
    }

    @Override
    protected <T extends JSONModel> void adapt(List<T> formModel, GraduationThesisTopicsAttributes attrs) {

    }
}
