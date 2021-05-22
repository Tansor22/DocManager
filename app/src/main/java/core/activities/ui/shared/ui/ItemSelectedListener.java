package core.activities.ui.shared.ui;

import android.view.View;
import android.widget.AdapterView;

@FunctionalInterface
public interface ItemSelectedListener extends AdapterView.OnItemSelectedListener {
    @Override
    void onItemSelected(AdapterView<?> parent, View view, int position, long id);

    @Override
    default void onNothingSelected(AdapterView<?> parent) {

    }
}
