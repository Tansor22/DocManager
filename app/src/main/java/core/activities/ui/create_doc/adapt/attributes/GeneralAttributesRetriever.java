package core.activities.ui.create_doc.adapt.attributes;

import api.clients.middleware.entity.Attributes;

import java.util.Map;

public class GeneralAttributesRetriever extends AttributesRetriever {
    @Override
    public Attributes retrieve(Map<String, String> data) {
        final String content = data.get("content");
        return new Attributes(content);
    }
}
