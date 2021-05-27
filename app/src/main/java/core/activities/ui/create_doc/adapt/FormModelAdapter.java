package core.activities.ui.create_doc.adapt;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.Document;
import com.shamweel.jsontoforms.models.JSONModel;
import lombok.AllArgsConstructor;

import java.util.List;

import static api.clients.middleware.adapt.DocTypesManager.classForType;
import static api.clients.middleware.adapt.DocTypesManager.hasValidType;

@AllArgsConstructor
public abstract class FormModelAdapter<A extends Attributes> {
    protected final Document document;

    public <T extends JSONModel> void adapt(List<T> formModel) {
        final Attributes attrs = document.getAttributes();
        hasValidType(attrs, classForType(document.getType()));
        adapt(formModel, (A) attrs);
    }

    protected abstract <T extends JSONModel> void adapt(List<T> formModel, A attrs);


    public static FormModelAdapter<?> of(Document document) {
        final String type = document.getType();
        switch (type) {
            case "General":
                return new GeneralFormModelAdapter(document);
            case "GraduationThesisTopics":
                return new GraduationThesisTopicsFormModelAdapter(document);
            default:
                throw new IllegalArgumentException("No form model adapter for doc with type = " + type);

        }
    }
}
