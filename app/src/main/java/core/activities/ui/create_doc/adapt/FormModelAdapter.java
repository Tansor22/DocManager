package core.activities.ui.create_doc.adapt;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.Document;
import com.shamweel.jsontoforms.FormConstants;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.ui.create_doc.adapt.attributes.GeneralAttributesRetriever;
import core.activities.ui.create_doc.adapt.attributes.GraduatedExpellingAttributesRetriever;
import core.activities.ui.create_doc.adapt.attributes.GraduationThesisTopicsAttributesRetriever;
import core.activities.ui.create_doc.adapt.attributes.PracticePermissionAttributesRetriever;
import core.activities.ui.shared.forms.FormAdapterEx;
import core.activities.ui.shared.forms.JSONModelEx;
import lombok.AllArgsConstructor;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static api.clients.middleware.adapt.DocTypesManager.classForType;
import static api.clients.middleware.adapt.DocTypesManager.hasValidType;
import static com.shamweel.jsontoforms.FormConstants.TYPE_CHECKBOX;
import static com.shamweel.jsontoforms.FormConstants.TYPE_RADIO;

@AllArgsConstructor
public abstract class FormModelAdapter<A extends Attributes> {
    protected final Document document;

    public final <T extends JSONModel> void adapt(List<T> formModel) {
        final Attributes attrs = document.getAttributes();
        hasValidType(attrs, classForType(document.getType()));
        // common fields + boilerplate
        for (T elementModel : formModel) {
            if ("title".equals(elementModel.getId())) {
                DataValueHashMap.put("title", document.getTitle());
            } else if ("signs".equals(elementModel.getId())) {
                DataValueHashMap.put("signs", String.join(",", document.getSignsRequired()));
            } else {
                adaptInternal(elementModel, (A) attrs);
            }
        }
    }

    protected abstract <T extends JSONModel> void adaptInternal(T model, A attrs);

    public static FormModelAdapter<?> of(Document document) {
        final String type = document.getType();
        switch (type) {
            case "General":
                return new GeneralFormModelAdapter(document);
            case "GraduationThesisTopics":
                return new GraduationThesisTopicsFormModelAdapter(document);
            case "PracticePermission":
                return new PracticePermissionFormModelAdapter(document);
            case "GraduatedExpelling":
                return new GraduatedExpellingFormModelAdapter(document);
            default:
                throw new IllegalArgumentException("No form model adapter for doc with type = " + type);
        }
    }

    protected String getDataSupplierUiRepresentation(Map<String, String> uiData) {
        return uiData.entrySet()
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }

    protected String getDataSupplierUiKey(JSONModelEx modelEx, String id) {
        return modelEx.getForm().stream()
                .filter(control ->
                        (control.getId().equals(id) && Arrays.asList(TYPE_CHECKBOX, TYPE_RADIO).contains(control.getType()))
                                || control.getId().equals(id + "_hint"))
                .findFirst()
                .map(JSONModel::getText)
                .orElse(id);
    }

    protected String getDataSupplierModelRepresentation(Map<String, String> modelData) {
        return new JSONObject(modelData).toString();
    }
}
