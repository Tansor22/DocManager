package core.activities.ui.shared.forms;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamweel.jsontoforms.adapters.FormAdapter;
import com.shamweel.jsontoforms.interfaces.JsonToFormClickListener;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.R;
import core.shared.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FormAdapterEx extends FormAdapter {
    Context backContext;
    List<JSONModel> backJsonModelList;
    public static final int TYPE_MULTI_SELECTION_SPINNER = 11;
    public static final int TYPE_MULTI_LINE_EDIT_TEXT = 12;

    public FormAdapterEx(List<JSONModel> jsonModelList, Context context, JsonToFormClickListener jsonToFormClickListener) {
        super(jsonModelList, context, jsonToFormClickListener);
        backContext = context;
        backJsonModelList = jsonModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_MULTI_SELECTION_SPINNER) {
            view = LayoutInflater.from(backContext).inflate(R.layout.item_multi_spinner, viewGroup, false);
            return new MultiSpinnerHolder(view, backJsonModelList);
        } else if (viewType == TYPE_MULTI_LINE_EDIT_TEXT) {
            view = LayoutInflater.from(backContext).inflate(R.layout.item_edittext, viewGroup, false);
            return new MultiLineEditTextViewHolder(view, backJsonModelList);
        }
        return super.onCreateViewHolder(viewGroup, viewType);
    }

    public int getItemViewType(int position) {
        int type = backJsonModelList.get(position).getType();
        if (type == TYPE_MULTI_SELECTION_SPINNER) {
            return TYPE_MULTI_SELECTION_SPINNER;
        } else if (type == TYPE_MULTI_LINE_EDIT_TEXT) {
            return TYPE_MULTI_LINE_EDIT_TEXT;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof MultiSpinnerHolder) {
            bindMultiSpinner((MultiSpinnerHolder) holder, position);
        } else if (holder instanceof MultiLineEditTextViewHolder) {
            postBindMultiLineEditText((MultiLineEditTextViewHolder) holder);
        }
    }

    private void postBindMultiLineEditText(MultiLineEditTextViewHolder holder) {
        final EditText editText = Objects.requireNonNull(holder.layoutEdittext.getEditText());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        // magic number
        editText.setMinLines(10);
        // to remove length restriction
        editText.setFilters(new InputFilter[]{});
    }

    private void bindMultiSpinner(MultiSpinnerHolder holder, int position) {
        JSONModel jsonModel = backJsonModelList.get(position);
        holder.spinnerTextView.setText(jsonModel.getText());
        holder.multiSpinner.setDialogTitle(jsonModel.getHint());
        holder.multiSpinner.setSelectAllButtonText(ApplicationContext.get().getString(R.string.select_all));
        holder.multiSpinner.setOkButtonText(ApplicationContext.get().getString(R.string.ok));
        holder.multiSpinner.setCancelButtonText(ApplicationContext.get().getString(R.string.cancel));
        List<String> spinnerItems = new ArrayList<>();
        for (int i = 0; i < jsonModel.getList().size(); i++) {
            spinnerItems.add(jsonModel.getList().get(i).getIndexText());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(backContext, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.multiSpinner.setAdapter(adapter);
        holder.multiSpinner.setItems(spinnerItems);

        if (!DataValueHashMap.getValue(jsonModel.getId()).isEmpty()) {
            int spinnerPosition = adapter.getPosition(DataValueHashMap.getValue(jsonModel.getId()));
            holder.multiSpinner.setSelection(spinnerPosition);
        } else {
            holder.multiSpinner.setSelection(0);
        }
    }
}
