package core.activities.ui.doc_details.model;

import androidx.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
public class SignDocResult {
    @Nullable
    private Reject reject;
    @Nullable
    private Approve approve;

    public SignDocResult(@Nullable Reject reject) {
        this.reject = reject;
    }

    public SignDocResult(@Nullable Approve approve) {
        this.approve = approve;
    }

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Reject {
        String reason;
    }

    @Data
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Approve {

    }
}
