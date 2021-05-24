package core.activities.ui.create_doc;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.request.GetFormConfigRequest;
import api.clients.middleware.response.GetFormConfigResponse;
import com.auth0.android.jwt.JWT;
import com.shamweel.jsontoforms.adapters.FormAdapter;
import com.shamweel.jsontoforms.interfaces.JsonToFormClickListener;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.R;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.forms.FormAdapterEx;
import core.activities.ui.shared.forms.JSONModelEx;
import core.activities.ui.shared.ui.ItemSelectedListener;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateDocumentWizardActivity extends AppCompatActivity implements UserMessageShower, Traceable {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FormAdapter mAdapter;
    List<JSONModel> jsonModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_document_wizard);
        Spinner docTypeSelectionSpinner = findViewById(R.id.docTypeSelectionSpinner);
        final List<String> docTypes = getIntent().getStringArrayListExtra("docTypes");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, docTypes);
        docTypeSelectionSpinner.setAdapter(adapter);
        docTypeSelectionSpinner.setOnItemSelectedListener((ItemSelectedListener) (parent, itemSelected, position, selectedId) -> {
            final String userDocType = adapter.getItem(position);
            DataValueHashMap.init();
            jsonModelList.clear();
            fetchFormConfig(HLFDataAdapter.fromUserDocumentType(userDocType));
        });
        recyclerView = findViewById(R.id.recyclerView);
        DataValueHashMap.init();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new FormAdapterEx(jsonModelList, this, new JsonToFormClickListener() {
            @Override
            public void onAddAgainButtonClick() {
                showUserMessage("Добавить студента");
            }

            @Override
            public void onSubmitButtonClick() {
                JSONObject jsonObject = new JSONObject(DataValueHashMap.dataValueHashMap);
                trace("onSubmitButtonClick: " + jsonObject.toString());


                for (Map.Entry<String, String> hashMap : DataValueHashMap.dataValueHashMap.entrySet()) {
                    String key = hashMap.getKey(); //  _id of the JSONOModel provided
                    String value = hashMap.getValue(); //value entered for the corresponding _id
                    trace("%s => %s", key, value);
                }
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void fetchFormConfig(String docType) {
        final JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get()).orElseThrow(IllegalStateException::new);
        Async.execute(() -> {
            try {
                final GetFormConfigResponse formConfig =
                        HLFMiddlewareAPIClient.getInstance().getFormConfig(new GetFormConfigRequest(docType), token.toString());
                jsonModelList.addAll(fixFormConfig(formConfig.getConfig()));
                runOnUiThread(() -> mAdapter.notifyDataSetChanged());
            } catch (HLFException e) {
                showUserMessage(R.string.unexpected_error);
            }
        });
    }

    private <T extends JSONModel> List<T> fixFormConfig(List<T> formConfig) {
        formConfig.stream()
                .filter(model -> "doc_type_spinner".equals(model.getId()))
                .findFirst()
                .ifPresent(self -> self.getList()
                        .forEach(listItem -> {
                            String docType = HLFDataAdapter.toUserDocumentType(listItem.getIndexText());
                            listItem.setIndexText(docType);
                        })
                );
        return formConfig;
    }
}