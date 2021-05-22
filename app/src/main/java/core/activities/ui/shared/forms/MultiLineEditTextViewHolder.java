package core.activities.ui.shared.forms;

import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.viewholder.EditTextViewHolder;

import java.util.List;
import java.util.Objects;

public class MultiLineEditTextViewHolder extends EditTextViewHolder {
    public MultiLineEditTextViewHolder(@NonNull View itemView, List<JSONModel> jsonModelList) {
        super(itemView, jsonModelList);
        final EditText editText = Objects.requireNonNull(layoutEdittext.getEditText());

        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setMinLines(10);
        // to remove length restriction
        editText.setFilters(new InputFilter[]{});
    }
}
