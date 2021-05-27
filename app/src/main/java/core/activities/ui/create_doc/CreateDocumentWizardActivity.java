package core.activities.ui.create_doc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.HLFMiddlewareAPIClient;
import api.clients.middleware.entity.Attributes;
import api.clients.middleware.entity.Document;
import api.clients.middleware.exception.HLFException;
import api.clients.middleware.request.GetFormConfigRequest;
import api.clients.middleware.request.NewDocRequest;
import api.clients.middleware.response.GetFormConfigResponse;
import com.auth0.android.jwt.JWT;
import com.shamweel.jsontoforms.adapters.FormAdapter;
import com.shamweel.jsontoforms.interfaces.JsonToFormClickListener;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import com.shamweel.jsontoforms.viewholder.CheckboxViewHolder;
import com.shamweel.jsontoforms.viewholder.EditTextViewHolder;
import com.shamweel.jsontoforms.viewholder.RadioViewHolder;
import com.shamweel.jsontoforms.viewholder.SpinnerViewHolder;
import core.activities.R;
import core.activities.ui.create_doc.adapt.FormModelAdapter;
import core.activities.ui.docs_to_sign.DocsToSignFragment;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.forms.CheckFieldValidations;
import core.activities.ui.shared.forms.FormAdapterEx;
import core.activities.ui.shared.forms.MultiSpinnerHolder;
import core.activities.ui.shared.ui.ItemSelectedListener;
import core.activities.ui.shared.ui.UiConstants;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.CommonUtils;
import core.shared.Traceable;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static com.shamweel.jsontoforms.sigleton.DataValueHashMap.dataValueHashMap;

public class CreateDocumentWizardActivity extends AppCompatActivity implements UserMessageShower, Traceable {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FormAdapter mAdapter;
    String docType;
    List<JSONModel> jsonModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_document_wizard);
        Spinner docTypeSelectionSpinner = findViewById(R.id.docTypeSelectionSpinner);
        final List<String> docTypes = getIntent().getStringArrayListExtra(UiConstants.DOC_TYPES_EXTRA);
        if (docTypes.size() <= 1) {
            docTypeSelectionSpinner.setEnabled(false);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, docTypes);
        docTypeSelectionSpinner.setAdapter(adapter);
        docTypeSelectionSpinner.setOnItemSelectedListener((ItemSelectedListener) (parent, itemSelected, position, selectedId) -> {
            final String userDocType = adapter.getItem(position);
            DataValueHashMap.init();
            jsonModelList.clear();
            Document document = getIntent().getParcelableExtra(UiConstants.DOC_TO_EDIT_EXTRA);
            fetchFormConfig(HLFDataAdapter.fromUserDocumentType(userDocType), document);
        });
        recyclerView = findViewById(R.id.recyclerView);
        setResult(RESULT_OK, null);
        DataValueHashMap.init();
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new FormAdapterEx(jsonModelList, this, new JsonToFormClickListener() {
            @Override
            public void onAddAgainButtonClick() {

            }

            @Override
            public void onSubmitButtonClick() {
                Intent intent = new Intent(CreateDocumentWizardActivity.this, DocsToSignFragment.class);
                intent.putExtra("answer", "Add again button was clicked lets pretend doc was edited");
                setResult(RESULT_OK, intent);
                finish();
                showUserMessage("Добавить студента");
                if (!CheckFieldValidations.isFieldsValidated(recyclerView, jsonModelList)) {
                    showUserMessage(R.string.validation_failed);
                    return;
                }
                // load pattern
                String patternFile = "pattern_" + docType + ".txt";
                final String pattern = CommonUtils.loadFileContentFromAsset(ApplicationContext.get(), patternFile);

                // parse DataValueHashMap.dataValueHashMap to build content
                final String content = String.format(pattern, DataValueHashMap.dataValueHashMap.get("doc_title"));
                trace("Content: " + content);
                Async.execute(() -> {
                    final JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get())
                            .orElseThrow(IllegalStateException::new);
                    List<String> signs = Arrays.asList(dataValueHashMap.get("doc_signs_multi_spinner").split(","));
                    final NewDocRequest newDocRequest = NewDocRequest.builder()
                            .title(dataValueHashMap.get("doc_title"))
                            .type(docType)
                            .owner(token.getClaim("member").asString())
                            .group(token.getClaim("group").asString())
                            .attributes(Attributes.builder()
                                    .content(content)
                                    .build())
                            .signsRequired(signs)
                            .build();
                    try {
                        HLFMiddlewareAPIClient.getInstance().newDoc(newDocRequest, token.toString());
                    } catch (HLFException e) {
                        showUserMessage(R.string.unexpected_error);
                    }
                    runOnUiThread(() -> {
                        showUserMessage(String.format(getString(R.string.doc_created_hint), dataValueHashMap.get("doc_title")));
                        clearForm();
                    });
                });
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

    // no need to clear inner forms as they do it automatic
    private void clearForm() {
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            final RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                if (holder instanceof EditTextViewHolder) {
                    ((EditTextViewHolder) holder).layoutEdittext.getEditText().setText("");
                } else if (holder instanceof RadioViewHolder) {
                    // select first item
                    ((RadioButton) ((RadioViewHolder) holder).rGroup.getChildAt(0)).setChecked(true);
                } else if (holder instanceof CheckboxViewHolder) {
                    // uncheck
                    ((CheckboxViewHolder) holder).checkBox.setChecked(false);
                } else if (holder instanceof SpinnerViewHolder) {
                    //select first item
                    ((SpinnerViewHolder) holder).spinner.setSelection(0);
                } else if (holder instanceof MultiSpinnerHolder) {
                    //select first item
                    ((MultiSpinnerHolder) holder).getMultiSpinner().setSelection(0);
                }
            }
        }
    }

    private void fetchFormConfig(String docType, Document document) {
        if (Objects.nonNull(document)) {
            Preconditions.checkArgument(docType.equals(document.getType()),
                    "Different types: form retched for type: " + docType + ", but doc's actual type: " + document.getStatus());
        }
        this.docType = docType;
        final JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get()).orElseThrow(IllegalStateException::new);
        Async.execute(() -> {
            try {
                final GetFormConfigResponse formConfig =
                        HLFMiddlewareAPIClient.getInstance().getFormConfig(new GetFormConfigRequest(docType), token.toString());
                adaptFormModel(formConfig.getConfig(), document);
                jsonModelList.addAll(formConfig.getConfig());
                runOnUiThread(() -> mAdapter.notifyDataSetChanged());
            } catch (HLFException e) {
                showUserMessage(R.string.unexpected_error);
            }
        });
    }

    private <T extends JSONModel> void adaptFormModel(List<T> formModel, Document document) {
        // change like so, each json input has type and id = doc.attributes.<id>
        if (Objects.nonNull(document)) {
            FormModelAdapter.adapt(formModel, document);
        } else {
            formModel.stream()
                    // todo change to doc_type
                    .filter(model -> "doc_type_spinner".equals(model.getId()))
                    .findFirst()
                    .ifPresent(self -> self.getList()
                            .forEach(listItem -> {
                                String docType = HLFDataAdapter.toUserDocumentType(listItem.getIndexText());
                                listItem.setIndexText(docType);
                            })
                    );
        }
    }
}