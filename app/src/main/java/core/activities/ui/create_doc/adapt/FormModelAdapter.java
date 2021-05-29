package core.activities.ui.create_doc.adapt;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.Document;
import com.shamweel.jsontoforms.FormConstants;
import com.shamweel.jsontoforms.models.JSONModel;
import core.activities.ui.shared.forms.FormAdapterEx;
import lombok.AllArgsConstructor;

import java.util.List;

import static api.clients.middleware.adapt.DocTypesManager.classForType;
import static api.clients.middleware.adapt.DocTypesManager.hasValidType;

@AllArgsConstructor
public abstract class FormModelAdapter<A extends Attributes> {
    protected final Document document;

    public final <T extends JSONModel> void adapt(List<T> formModel) {
        final Attributes attrs = document.getAttributes();
        hasValidType(attrs, classForType(document.getType()));
        // common fields + boilerplate
        for (T elementModel : formModel) {
            if ("title".equals(elementModel.getId())) {
                set(elementModel, document.getTitle());
            } else if ("signs".equals(elementModel.getId())) {
                set(elementModel, String.join(", ", document.getSignsRequired()));
            } else {
                adaptInternal(elementModel, (A) attrs);
            }
        }
    }

    protected abstract <T extends JSONModel> void adaptInternal(T model, A attrs);

    // should be able to work with models of all types
    protected final <T extends JSONModel> void set(T model, String value) {
        switch (model.getType()) {
            case FormAdapterEx.TYPE_MULTI_SELECTION_SPINNER:
            case FormConstants.TYPE_TEXT: {
                model.setText(value);
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported model type: " + model.getType());
        }
    }

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