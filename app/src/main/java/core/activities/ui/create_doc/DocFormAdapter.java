package core.activities.ui.create_doc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shamweel.jsontoforms.adapters.FormAdapter;
import com.shamweel.jsontoforms.interfaces.JsonToFormClickListener;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.R;

import java.util.ArrayList;
import java.util.List;

public class DocFormAdapter extends FormAdapter {
    Context context2;
    List<JSONModel> jsonModelList2;
    public static final int TYPE_MULTI_SPINNER = 11;

    public DocFormAdapter(List<JSONModel> jsonModelList, Context context, JsonToFormClickListener jsonToFormClickListener) {
        super(jsonModelList, context, jsonToFormClickListener);
        context2 = context;
        jsonModelList2 = jsonModelList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_MULTI_SPINNER) {
            view = LayoutInflater.from(context2).inflate(R.layout.item_multi_spinner, viewGroup, false);
            return new MultiSpinnerHolder(view, jsonModelList2);
        }
        return super.onCreateViewHolder(viewGroup, viewType);
    }

    public int getItemViewType(int position) {
        int type = jsonModelList2.get(position).getType();
        if (type == TYPE_MULTI_SPINNER) {
            return TYPE_MULTI_SPINNER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (holder instanceof MultiSpinnerHolder) {
            bindMultiSpinner((MultiSpinnerHolder) holder, position);
        }
    }

    private void bindMultiSpinner(MultiSpinnerHolder holder, int position) {
        JSONModel jsonModel = jsonModelList2.get(position);
        holder.spinnerTextView.setText(jsonModel.getText());
        List<String> categoriesSpin = new ArrayList<>();
        for (int i = 0; i < jsonModel.getList().size(); i++) {
            categoriesSpin.add(jsonModel.getList().get(i).getIndexText());
        }
        ArrayAdapter<String> dataAdapterVisit;
        dataAdapterVisit = new ArrayAdapter<>(context2, android.R.layout.simple_spinner_item, categoriesSpin);
        dataAdapterVisit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.multiSpinner.setAdapter(dataAdapterVisit);
        holder.multiSpinner.setItems(categoriesSpin);

       if (!DataValueHashMap.getValue(jsonModel.getId()).isEmpty()) {
            int spinnerPosition = dataAdapterVisit.getPosition(DataValueHashMap.getValue(jsonModel.getId()));
            holder.multiSpinner.setSelection(spinnerPosition);
        } else {
            holder.multiSpinner.setSelection(0);
        }
    }
}
