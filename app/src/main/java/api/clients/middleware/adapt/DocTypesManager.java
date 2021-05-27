package api.clients.middleware.adapt;

import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.GraduationThesisTopicsAttributes;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

public class DocTypesManager {
    private static final Map<String, Class<? extends Attributes>> MAPPING = new ImmutableMap.Builder<String, Class<? extends Attributes>>()
            // provide type => class mappings here
            .put("General", Attributes.class)
            .put("GraduationThesisTopics", GraduationThesisTopicsAttributes.class)
            .build();

    public static Class<? extends Attributes> classForType(String type) {
        return Optional.ofNullable(MAPPING.get(type))
                .orElseThrow(() -> new IllegalArgumentException("No class defined for type = " + type));
    }

    /**
     * If method returns true, it means casting {@code attrs} is valid and won't throw an exception.
     * Using as follows
     * <code>
     * Class<?> clazz = DocTypesManager.classForType(type);
     * GraduationThesisTopicsAttributes attrs = (GraduationThesisTopicsAttributes) DocTypesManager.hasValidType(attrs, clazz);
     * <code/>
     *
     * @param attrs
     * @param clazz
     * @return
     */
    public static Attributes hasValidType(Attributes attrs, Class<? extends Attributes> clazz) {
        if (clazz.isInstance(attrs)) {
            return attrs;
        } else {
            throw new IllegalArgumentException(attrs + " is not instance of " + clazz.getSimpleName());
        }
    }
}
