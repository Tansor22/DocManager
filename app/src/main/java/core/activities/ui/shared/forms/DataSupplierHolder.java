package core.activities.ui.shared.forms;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.shamweel.jsontoforms.models.JSONModel;

import java.util.List;

public class DataSupplierHolder extends InnerFormHolder {
    public DataSupplierHolder(View view, List<JSONModel> jsonModelList) {
        super(view, jsonModelList);
    }
}
