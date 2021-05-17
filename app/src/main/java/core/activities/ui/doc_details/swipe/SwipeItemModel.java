package core.activities.ui.doc_details.swipe;

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
    // change
    int image;
    String nama, usia, kota;
}
