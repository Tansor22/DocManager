package core.activities.ui.docs_to_sign.swipe;

import api.clients.middleware.entity.Document;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@Data
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SwipeItemModel {
    Document document;
}
