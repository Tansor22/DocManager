package core.activities.ui.create_doc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
import api.clients.middleware.request.ChangeDocRequest;
import api.clients.middleware.request.GetFormConfigRequest;
import api.clients.middleware.request.NewDocRequest;
import api.clients.middleware.response.GetFormConfigResponse;
import com.auth0.android.jwt.JWT;
import com.shamweel.jsontoforms.adapters.FormAdapter;
import com.shamweel.jsontoforms.interfaces.JsonToFormClickListener;
import com.shamweel.jsontoforms.models.JSONModel;
import com.shamweel.jsontoforms.sigleton.DataValueHashMap;
import core.activities.R;
import core.activities.ui.create_doc.adapt.FormModelAdapter;
import core.activities.ui.create_doc.adapt.attributes.AttributesRetriever;
import core.activities.ui.docs_to_sign.DocsToSignFragment;
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.forms.FormAdapterEx;
import core.activities.ui.shared.forms.FormUtils;
import core.activities.ui.shared.ui.ItemSelectedListener;
import core.activities.ui.shared.ui.UiConstants;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.shamweel.jsontoforms.sigleton.DataValueHashMap.dataValueHashMap;
import static core.activities.ui.shared.ui.UiConstants.EDITED_DOC_TITLE_EXTRA;

public class CreateDocumentWizardActivity extends AppCompatActivity implements UserMessageShower, Traceable {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FormAdapter adapter;
    String docType;
    List<JSONModel> jsonModelList = new ArrayList<>();
    private boolean needUpdate;
    String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_document_wizard);
        Spinner docTypeSelectionSpinner = findViewById(R.id.docTypeSelectionSpinner);
        final List<String> docTypes = getIntent().getStringArrayListExtra(UiConstants.DOC_TYPES_EXTRA);
        if (docTypes.size() <= 1) {
            docTypeSelectionSpinner.setEnabled(false);
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, docTypes.stream()
                        .map(HLFDataAdapter::toUserDocumentType)
                        .collect(Collectors.toList()));
        docTypeSelectionSpinner.setAdapter(adapter);
        docTypeSelectionSpinner.setOnItemSelectedListener((ItemSelectedListener) (parent, itemSelected, position, selectedId) -> {
            final String userDocType = adapter.getItem(position);
            DataValueHashMap.init();
            jsonModelList.clear();
            Document document = getIntent().getParcelableExtra(UiConstants.DOC_TO_EDIT_EXTRA);
            docId = Objects.nonNull(document) ? document.getDocumentId() : null;
            fetchFormConfig(HLFDataAdapter.fromUserDocumentType(userDocType), document);
        });
        recyclerView = findViewById(R.id.recyclerView);
        setResult(RESULT_OK, null);
        DataValueHashMap.init();
        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        if (needUpdate)
            Async.execute(() -> MainActivity.getModel().getDocuments());
        super.onDestroy();
    }

    private void initRecyclerView() {
        adapter = new FormAdapterEx(jsonModelList, this, new JsonToFormClickListener() {
            @Override
            public void onAddAgainButtonClick() {

            }

            @Override
            public void onSubmitButtonClick() {
                if (!FormUtils.isFieldsValidated(recyclerView, jsonModelList)) {
                    showUserMessage(R.string.validation_failed);
                    return;
                }

                Async.execute(() -> {
                    final JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get())
                            .orElseThrow(IllegalStateException::new);
                    List<String> signs = Arrays.asList(dataValueHashMap.get("signs").split(","));
                    final Attributes attributes = AttributesRetriever.of(docType).retrieve(dataValueHashMap);
                   if (StringUtils.isNotEmpty(docId)) {
                        final ChangeDocRequest request = ChangeDocRequest.builder()
                                .documentId(docId)
                                .type("EDIT")
                                .member(token.getClaim("member").asString())
                                .details("Изменено участником " + token.getClaim("member").asString())
                                // todo calc diff
                                .attributes(attributes)
                                .build();
                      try {
                            HLFMiddlewareAPIClient.getInstance().changeDoc(request, token.toString());
                            needUpdate = true;
                            // back to activity called
                            Intent intent = new Intent(CreateDocumentWizardActivity.this, DocsToSignFragment.class);
                            intent.putExtra(EDITED_DOC_TITLE_EXTRA, dataValueHashMap.get("title"));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (HLFException e) {
                            showUserMessage(R.string.unexpected_error);
                        }
                    } else {
                        final NewDocRequest newDocRequest = NewDocRequest.builder()
                                .title(dataValueHashMap.get("title"))
                                .type(docType)
                                .owner(token.getClaim("member").asString())
                                .group(token.getClaim("group").asString())
                                .attributes(attributes)
                                .signsRequired(signs)
                                .build();
                        try {
                            HLFMiddlewareAPIClient.getInstance().newDoc(newDocRequest, token.toString());
                            needUpdate = true;
                        } catch (HLFException e) {
                            showUserMessage(R.string.unexpected_error);
                        }
                        runOnUiThread(() -> {
                            showUserMessage(String.format(getString(R.string.doc_created_hint), dataValueHashMap.get("title")));
                            FormUtils.clearForm(recyclerView, adapter);
                        });
                    }
                });
            }
        });
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (HLFException e) {
                showUserMessage(R.string.unexpected_error);
            }
        });
    }

    private <T extends JSONModel> void adaptFormModel(List<T> formModel, Document document) {
        if (StringUtils.isNotEmpty(docId) && Objects.nonNull(document)) {
            FormModelAdapter.of(document).adapt(formModel);
            List<String> inputsToDisable = Arrays.asList("title", "signs");
            formModel.stream()
                    .filter(model -> inputsToDisable.contains(model.getId()))
                    .forEach(model -> model.setEditable(false));
        }
    }
}