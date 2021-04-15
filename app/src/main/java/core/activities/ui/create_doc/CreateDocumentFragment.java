package core.activities.ui.create_doc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shamweel.jsontoforms.adapters.FormAdapter;
import com.shamweel.jsontoforms.interfaces.JsonToFormClickListener;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import com.shamweel.jsontoforms.validate.CheckFieldValidations;
import core.activities.R;
import core.sessions.SessionConstants;
import core.sessions.SessionManager;
import core.shared.JsonUtils;
import core.shared.Traceable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateDocumentFragment extends Fragment implements Traceable, JsonToFormClickListener {

    private CreateDocumentModel createDocumentModel;
    RecyclerView recyclerView;
    FormAdapter mAdapter;
    List<JSONModel> jsonModelList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createDocumentModel = new ViewModelProvider(this).get(CreateDocumentModel.class);
        View root = inflater.inflate(R.layout.fragment_create_doc, container, false);
        // form init
        recyclerView = root.findViewById(R.id.recyclerView);
        DataValueHashMap.init();
        initRecyclerView();
        fetchData();
        return root;
    }

    private void initRecyclerView() {
        mAdapter = new FormAdapter(jsonModelList, getContext(), this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void fetchData() {
        String formSource = "document_form.json";
        final String org = SessionManager.get(requireContext(), SessionConstants.ORG);
        if (org == null || org.length() == 0) {
            trace("No org!");
        }
        if ("org2".equals(org)) {
            formSource = "document_form2.json";
        }
        String json = JsonUtils.loadJSONFromAsset(requireContext(), formSource);
        List<JSONModel> jsonModelList1 = new Gson().fromJson(json, new TypeToken<List<JSONModel>>() {
        }.getType());
        jsonModelList.addAll(jsonModelList1);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAddAgainButtonClick() {
        Toast.makeText(requireContext(), "Add again button click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubmitButtonClick() {
        if (!CheckFieldValidations.isFieldsValidated(recyclerView, jsonModelList)) {
            Toast.makeText(requireContext(), "Validation Failed", Toast.LENGTH_SHORT).show();
            return;
        }


        //Combined Data
        JSONObject jsonObject = new JSONObject(DataValueHashMap.dataValueHashMap);
        trace("onSubmitButtonClick: %s", jsonObject.toString());


        //If single value required for corresponding _id's
        for (Map.Entry<String, String> hashMap : DataValueHashMap.dataValueHashMap.entrySet()) {
            String key = hashMap.getKey(); //  _id of the JSONOModel provided
            String value = hashMap.getValue(); //value entered for the corresponding _id
            trace("%s => %s", key, value);
        }

    }
}