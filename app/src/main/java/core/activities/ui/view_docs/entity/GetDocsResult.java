package core.activities.ui.view_docs.entity;

import androidx.annotation.Nullable;
import api.clients.middleware.entity.Document;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetDocsResult {
    @Nullable
    List<Document> documents;
    @Nullable
    Integer error;
}
