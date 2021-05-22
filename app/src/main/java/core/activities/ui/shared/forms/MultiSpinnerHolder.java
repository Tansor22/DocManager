package core.activities.ui.shared.forms;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.ui.MultiSelectionSpinner;
import lombok.Getter;

import java.util.List;

public class MultiSpinnerHolder extends RecyclerView.ViewHolder implements UserMessageShower {
    @Getter
    MultiSelectionSpinner multiSpinner;
    @Getter
    TextView spinnerTextView;

    public MultiSpinnerHolder(@NonNull View itemView, List<JSONModel> jsonModelList) {
        super(itemView);
        multiSpinner = itemView.findViewById(R.id.multiSpinner);
        spinnerTextView = itemView.findViewById(R.id.spinnerTextView);
        multiSpinner.setListener(new MultiSelectionSpinner.OnMultipleItemsSelectedListener() {
            @Override
            public void selectedIndices(List<Integer> indices, MultiSelectionSpinner spinner) {
            }

            @Override
            public void selectedStrings(List<String> strings, MultiSelectionSpinner spinner) {
                if (getAbsoluteAdapterPosition() == -1) {
                    return;
                }
                DataValueHashMap.put(
                        jsonModelList.get(getBindingAdapterPosition()).getId(),
                        String.join(",", strings));
                if (itemView.getRootView().findFocus() != null) {
                    itemView.getRootView().findFocus().clearFocus();
                }
            }
        });
    }
}
