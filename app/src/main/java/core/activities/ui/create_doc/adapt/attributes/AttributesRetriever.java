package core.activities.ui.create_doc.adapt.attributes;

import api.clients.middleware.entity.Attributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.shared.Traceable;

import java.util.Map;

public abstract class AttributesRetriever implements Traceable {
    protected Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static AttributesRetriever of(String type) {
        switch (type) {
            case "General":
                return new GeneralAttributesRetriever();
            case "GraduationThesisTopics":
                return new GraduationThesisTopicsAttributesRetriever();
            case "PracticePermission":
                return new PracticePermissionAttributesRetriever();
            case "GraduatedExpelling":
                return new GraduatedExpellingAttributesRetriever();
            default:
                throw new IllegalArgumentException("No attributes retriever defined for doc with type = " + type);

        }
    }

    public Attributes retrieve(Map<String, String> data) {
        trace("Data got: %s", gson.toJson(data));
        return retrieveInternal(data);
    }

    public abstract Attributes retrieveInternal(Map<String, String> data);
}
