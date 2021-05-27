package api.clients.middleware.json;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.DocTypeHolder;
import api.clients.middleware.entity.Document;
import com.google.gson.*;

import java.lang.reflect.Type;


public class DocumentAdapter implements /*JsonSerializer<Document>,*/ JsonDeserializer<Document> {

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    @Override
    public Document deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        final String type = jsonObject.getAsJsonPrimitive("type").getAsString();

        final Document deserialized = gson.fromJson(json, Document.class);
        // bind attributes
        final JsonObject attributes = jsonObject.getAsJsonObject("attributes");
        // actual type should be right
        final Object deserializedAttrs = context.deserialize(attributes, DocTypeHolder.INSTANCE.get(type));
        deserialized.setAttributes((Attributes) deserializedAttrs);
        return deserialized;
    }
}
