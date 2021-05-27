package core.activities.ui.shared.forms;

import android.widget.Adapter;
import android.widget.RadioButton;
import androidx.recyclerview.widget.RecyclerView;
import com.shamweel.jsontoforms.FormConstants;
import com.shamweel.jsontoforms.adapters.FormAdapter;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import com.shamweel.jsontoforms.viewholder.CheckboxViewHolder;
import com.shamweel.jsontoforms.viewholder.EditTextViewHolder;
import com.shamweel.jsontoforms.viewholder.RadioViewHolder;
import com.shamweel.jsontoforms.viewholder.SpinnerViewHolder;
import core.activities.R;
import core.shared.ApplicationContext;

import java.util.List;

public class FormUtils {
    // can be extended
    public static void clearForm(RecyclerView recyclerView, FormAdapter adapter) {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            final RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                if (holder instanceof EditTextViewHolder) {
                    ((EditTextViewHolder) holder).layoutEdittext.getEditText().setText("");
                } else if (holder instanceof RadioViewHolder) {
                    // select first item
                    ((RadioButton) ((RadioViewHolder) holder).rGroup.getChildAt(0)).setChecked(true);
                } else if (holder instanceof CheckboxViewHolder) {
                    // uncheck
                    ((CheckboxViewHolder) holder).checkBox.setChecked(false);
                } else if (holder instanceof SpinnerViewHolder) {
                    //select first item
                    ((SpinnerViewHolder) holder).spinner.setSelection(0);
                } else if (holder instanceof MultiSpinnerHolder) {
                    //select first item
                    ((MultiSpinnerHolder) holder).getMultiSpinner().setSelection(0);
                }
            }
        }
    }


    // no need to do inner forms validation as they are not required
    public static boolean isFieldsValidated(RecyclerView recyclerView, List<JSONModel> jsonModelList) {
        final boolean[] isValidated = {true};

        for (int i = 0; i < jsonModelList.size(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                if (viewHolder instanceof EditTextViewHolder) {
                    ((EditTextViewHolder) viewHolder).layoutEdittext.setErrorEnabled(false);
                } else if (viewHolder instanceof RadioViewHolder) {
                    for (int j = 0; j < ((RadioViewHolder) viewHolder).rGroup.getChildCount(); j++) {
                        ((RadioButton) ((RadioViewHolder) viewHolder).rGroup.getChildAt(j)).setError(null);
                    }
                } else if (viewHolder instanceof CheckboxViewHolder) {
                    ((CheckboxViewHolder) viewHolder).checkBox.setError(null);
                }
            }
        }

        for (int i = 0; i < jsonModelList.size(); i++) {

            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);

            JSONModel jsonModel = jsonModelList.get(i);
            String fieldValue = DataValueHashMap.getValue(jsonModel.getId());
            String EMPTY_STRING = "";


            if (jsonModel.getRequired() != null && jsonModel.getRequired()) {
                if (viewHolder != null) {
                    if ((jsonModel.getType() == FormConstants.TYPE_EDITTEXT || jsonModel.getType() == FormAdapterEx.TYPE_MULTI_LINE_EDIT_TEXT)
                            && fieldValue.equalsIgnoreCase(EMPTY_STRING)) {
                        ((EditTextViewHolder) viewHolder).layoutEdittext.setErrorEnabled(true);
                        ((EditTextViewHolder) viewHolder).layoutEdittext.setError(ApplicationContext.get().getString(R.string.required_field));
                        recyclerView.smoothScrollToPosition(i);
                        isValidated[0] = false;

                    } else if (jsonModel.getType() == FormConstants.TYPE_CHECKBOX
                            && fieldValue.equalsIgnoreCase(EMPTY_STRING)) {
                        ((CheckboxViewHolder) viewHolder).checkBox.setError(ApplicationContext.get().getString(R.string.required_field));
                        recyclerView.smoothScrollToPosition(i);
                        isValidated[0] = false;

                    } else if (jsonModel.getType() == FormConstants.TYPE_RADIO
                            && fieldValue.equalsIgnoreCase(EMPTY_STRING)) {
                        for (int j = 0; j < ((RadioViewHolder) viewHolder).rGroup.getChildCount(); j++) {
                            ((RadioButton) ((RadioViewHolder) viewHolder).rGroup.getChildAt(j)).setError(ApplicationContext.get().getString(R.string.required_field));
                        }
                        recyclerView.smoothScrollToPosition(i);
                        isValidated[0] = false;
                    }

                }
            }
        }
        return isValidated[0];
    }
}
