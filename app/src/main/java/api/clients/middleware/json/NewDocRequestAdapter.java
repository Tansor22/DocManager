package api.clients.middleware.json;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.DocTypeHolder;
import api.clients.middleware.entity.Document;
import api.clients.middleware.request.NewDocRequest;
import com.google.gson.*;

import java.lang.reflect.Type;

public class NewDocRequestAdapter implements JsonSerializer<NewDocRequest> {

    @Override
    public JsonElement serialize(NewDocRequest src, Type typeOfSrc, JsonSerializationContext context) {
        final Object castedAttrs = DocTypeHolder.INSTANCE.get(src.getType()).cast(src.getAttributes());
        return null;
    }
}
