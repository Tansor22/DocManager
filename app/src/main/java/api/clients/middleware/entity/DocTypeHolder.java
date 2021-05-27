package api.clients.middleware.entity;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class DocTypeHolder {
    public static final Map<String, Class<? extends Attributes>> INSTANCE
            = ImmutableMap.of("General", Attributes.class,
            "GraduationThesisTopics", GraduationThesisTopicsAttributes.class);
}
