package core.activities.ui.create_doc;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;
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
                showUserMessage("Selected: " + strings.toString());
                if (getAdapterPosition() == -1) {
                    return;
                }

                DataValueHashMap.put(
                        jsonModelList.get(getBindingAdapterPosition()).getId(),
                        strings.toString());

                if (itemView.getRootView().findFocus() != null) {
                    itemView.getRootView().findFocus().clearFocus();
                }
            }

            //@Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (getAdapterPosition() == -1) {
                    return;
                }

                DataValueHashMap.put(
                        jsonModelList.get(getBindingAdapterPosition()).getId(),
                        multiSpinner.getSelectedItem().toString());

                if (itemView.getRootView().findFocus() != null) {
                    itemView.getRootView().findFocus().clearFocus();
                }
            }

            // @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
