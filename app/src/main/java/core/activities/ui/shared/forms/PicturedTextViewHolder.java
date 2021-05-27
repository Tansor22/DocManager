package core.activities.ui.shared.forms;

import android.view.View;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import com.shamweel.jsontoforms.viewholder.TextViewHolder;
import core.activities.R;

import java.util.List;
import java.util.Objects;

public class PicturedTextViewHolder extends TextViewHolder {
    public PicturedTextViewHolder(View view, List<JSONModel> jsonModelList) {
        super(view, jsonModelList);
        view.findViewById(R.id.txtHead).setOnClickListener(self-> {
            if (getAbsoluteAdapterPosition() == -1) {
                return;
            }
            final JSONModel model = jsonModelList.get(getBindingAdapterPosition());
            jsonModelList.remove(model);
            Objects.requireNonNull(getBindingAdapter()).notifyDataSetChanged();
            DataValueHashMap.dataValueHashMap.remove(model.getId());
        });
    }
}
