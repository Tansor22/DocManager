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
import core.activities.ui.shared.UserMessageShower;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class FormAdapterEx extends FormAdapter implements UserMessageShower, Traceable {
    Context backContext;
    List<JSONModel> backJsonModelList;
    public static final int TYPE_MULTI_SELECTION_SPINNER = 11;
    public static final int TYPE_MULTI_LINE_EDIT_TEXT = 12;
    public static final int TYPE_INNER_FORM = 13;
    public static final int TYPE_PICTURED_TEXT_VIEW = 14;
    public static final int TYPE_DATA_SUPPLIER = 15;
    //hack
    int counter;


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
        } else if (viewType == TYPE_INNER_FORM) {
            view = LayoutInflater.from(backContext).inflate(R.layout.item_inner_form, viewGroup, false);
            return new InnerFormHolder(view, backJsonModelList);
        } else if (viewType == TYPE_PICTURED_TEXT_VIEW) {
            view = LayoutInflater.from(backContext).inflate(R.layout.item_textview, viewGroup, false);
            return new PicturedTextViewHolder(view, backJsonModelList);
        } else if (viewType == TYPE_DATA_SUPPLIER) {
            view = LayoutInflater.from(backContext).inflate(R.layout.item_inner_form, viewGroup, false);
            return new DataSupplierHolder(view, backJsonModelList);
        }
        return super.onCreateViewHolder(viewGroup, viewType);
    }

    public int getItemViewType(int position) {
        int type = backJsonModelList.get(position).getType();
        if (type == TYPE_MULTI_SELECTION_SPINNER) {
            return TYPE_MULTI_SELECTION_SPINNER;
        } else if (type == TYPE_MULTI_LINE_EDIT_TEXT) {
            return TYPE_MULTI_LINE_EDIT_TEXT;
        } else if (type == TYPE_INNER_FORM) {
            return TYPE_INNER_FORM;
        } else if (type == TYPE_PICTURED_TEXT_VIEW) {
            return TYPE_PICTURED_TEXT_VIEW;
        } else if (type == TYPE_DATA_SUPPLIER) {
            return TYPE_DATA_SUPPLIER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof MultiSpinnerHolder) {
            bindMultiSpinner((MultiSpinnerHolder) holder, position);
        } else if (holder instanceof MultiLineEditTextViewHolder) {
            postBindMultiLineEditText((MultiLineEditTextViewHolder) holder, position);
        } else if (holder instanceof InnerFormHolder) {
            bindInnerFormHolder((InnerFormHolder) holder, position);
        } else if (holder instanceof PicturedTextViewHolder) {
            postBindPicturedTextViewHolder((PicturedTextViewHolder) holder, position);
        }
    }

    private void postBindPicturedTextViewHolder(PicturedTextViewHolder holder, int position) {
        holder.txtHead.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reject_md, 0, 0, 0);
    }

    // actually binds data supplier
    private void bindInnerFormHolder(InnerFormHolder holder, int position) {
        JSONModelEx formModel = (JSONModelEx) backJsonModelList.get(position);
        final List<JSONModel> formControlsModel = formModel.getForm();
        FormAdapter adapter = new FormAdapterEx(formControlsModel, holder.recyclerView.getContext(), new JsonToFormClickListener() {

            private String getUiRepresentation(Map<String, String> uiData) {
                return uiData.entrySet()
                        .stream()
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .collect(Collectors.joining("\n"));
            }

            private String getModelRepresentation(Map<String, String> modelData) {
                return new JSONObject(modelData).toString();
            }

            @Override
            public void onAddAgainButtonClick() {
                final List<String> ids = formControlsModel.stream()
                        .map(JSONModel::getId)
                        // skip data supplier models
                        .filter(id -> !id.startsWith("_data_"))
                        .collect(Collectors.toList());
                Map<String, String> uiData = new HashMap<>();
                Map<String, String> modelData = new HashMap<>();
                ids.forEach(id -> {
                    final String value = DataValueHashMap.dataValueHashMap.get(id);
                    if (value != null) {
                        // using '<id>_hint' or '<id>' as uiKey
                        String uiKey = formControlsModel.stream()
                                .filter(control -> control.getId().equals(id + "_hint"))
                                .findFirst()
                                .map(JSONModel::getText)
                                .orElse(id);
                        uiData.put(uiKey, value);
                        modelData.put(id, value);
                    }
                });
                final JSONModel dataModel = JSONModelEx.picturedTextView(
                        // _data_student_1, _data_student_2
                        "_data_" + formModel.getId() + "_" + (counter++),
                        getUiRepresentation(uiData));
                DataValueHashMap.dataValueHashMap.put(dataModel.getId(), getModelRepresentation(modelData));
                formControlsModel.add(dataModel);

                notifyDataSetChanged();
                // need use this instead of notifyDataSetChanged to avoid dirty hacks with counter,
                // but this crashes app because of some android bug https://stackoverflow.com/questions/35653439/recycler-view-inconsistency-detected-invalid-view-holder-adapter-positionviewh
                //notifyItemInserted(formControlsModel.size() - 1);
            }

            @Override
            public void onSubmitButtonClick() {
            }
        });
        holder.recyclerView.setAdapter(adapter);
    }

    private void postBindMultiLineEditText(MultiLineEditTextViewHolder holder, int position) {
        final EditText editText = Objects.requireNonNull(holder.layoutEdittext.getEditText());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        // max length in Multiline edit means rows, not symbols
        editText.setMinLines(backJsonModelList.get(position).getMaxLength());
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
