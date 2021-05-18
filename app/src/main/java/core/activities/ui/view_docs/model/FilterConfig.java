package core.activities.ui.view_docs.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FilterConfig {
    boolean isCompletedDocsShown;
    boolean isRejectedDocsShown;

    public static final FilterConfig DEFAULT = new FilterConfig(true, true);
}
