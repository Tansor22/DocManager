package core.activities.ui.shared.forms;

import android.view.View;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.viewholder.TextViewHolder;

import java.util.List;

public class PicturedTextViewHolder extends TextViewHolder {
    public PicturedTextViewHolder(View view, List<JSONModel> backJsonModelList) {
        super(view, backJsonModelList);
    }
}
