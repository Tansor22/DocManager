package core.activities.ui.shared.forms;

import android.view.View;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.shamweel.jsontoforms.interfaces.JsonToFormClickListener;
import com.shamweel.jsontoforms.models.JSONModel;
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;
import lombok.Getter;

import java.util.List;

@Getter
public class InnerFormHolder extends RecyclerView.ViewHolder implements UserMessageShower {
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    public InnerFormHolder(View view, List<JSONModel> jsonModelList) {
        super(view);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
