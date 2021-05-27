package core.activities.ui.create_doc.adapt.attributes;

import api.clients.middleware.entity.Attributes;

import java.util.Map;

public abstract class AttributesRetriever {

    public static AttributesRetriever of(String type) {
        switch (type) {
            case "General":
                return new GeneralAttributesRetriever();
            case "GraduationThesisTopics":
                return new GraduationThesisTopicsAttributesRetriever();
            default:
                throw new IllegalArgumentException("No attributes retriever defined for doc with type = " + type);

        }
    }
    public abstract Attributes retrieve(Map<String, String> data);
}
