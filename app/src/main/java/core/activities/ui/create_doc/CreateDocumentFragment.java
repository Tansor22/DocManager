package core.activities.ui.create_doc;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import core.activities.R;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.forms.CheckFieldValidations;
import core.activities.ui.shared.forms.FormAdapterEx;
import core.shared.JsonUtils;
import core.shared.Traceable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateDocumentFragment extends Fragment implements Traceable, JsonToFormClickListener, UserMessageShower {

    private CreateDocumentModel model;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FormAdapter mAdapter;
    List<JSONModel> jsonModelList = new ArrayList<>();

    private void showGotoWizardOption() {
        CheckBox dontShowAnymoreCheckBox = new CheckBox(requireContext());
        dontShowAnymoreCheckBox.setText(R.string.dont_show_anymore);
        dontShowAnymoreCheckBox.setChecked(false);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.goto_wizard_title)
                .setView(dontShowAnymoreCheckBox)
                .setPositiveButton(R.string.answer_yes, (dialog, ignored) -> {
                    // todo go to wizard
                    trace("Dont show anymore " + dontShowAnymoreCheckBox.isChecked());
                })
                .setNegativeButton(R.string.answer_no, (dialog, ignored) -> {
                    trace("Dont show anymore " + dontShowAnymoreCheckBox.isChecked());
                }).show().setCanceledOnTouchOutside(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        model = new ViewModelProvider(this).get(CreateDocumentModel.class);
        View root = inflater.inflate(R.layout.fragment_create_doc, container, false);
        // form init
        recyclerView = root.findViewById(R.id.recyclerView);
        DataValueHashMap.init();
        initRecyclerView();
        fetchData();
        return root;
    }

    private void initRecyclerView() {
        mAdapter = new FormAdapterEx(jsonModelList, getContext(), this);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void fetchData() {
        String formSource = "document_form.json";
        String json = JsonUtils.loadJSONFromAsset(requireContext(), formSource);
        List<JSONModel> jsonModelList1 = new Gson().fromJson(json, new TypeToken<List<JSONModel>>() {}.getType());
        // signs added
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