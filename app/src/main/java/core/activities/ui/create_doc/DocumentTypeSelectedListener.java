package core.activities.ui.create_doc;

import android.view.View;
import android.widget.AdapterView;

@FunctionalInterface
public interface DocumentTypeSelectedListener extends AdapterView.OnItemSelectedListener {
    @Override
    void onItemSelected(AdapterView<?> parent, View view, int position, long id);

    @Override
    default void onNothingSelected(AdapterView<?> parent) {

    }
}
