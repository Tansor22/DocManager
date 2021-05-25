package core.activities.ui.create_doc;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.HLFDataAdapter;
import api.clients.middleware.HLFMiddlewareAPIClient;
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
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.forms.CheckFieldValidations;
import core.activities.ui.shared.forms.FormAdapterEx;
import core.activities.ui.shared.forms.MultiSpinnerHolder;
import core.sessions.SessionConstants;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shamweel.jsontoforms.sigleton.DataValueHashMap.dataValueHashMap;

public class CreateDocumentFragment extends Fragment implements Traceable, JsonToFormClickListener, UserMessageShower {

    private CreateDocumentModel model;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FormAdapter mAdapter;
    List<JSONModel> jsonModelList = new ArrayList<>();
    private boolean needUpdate;
    static String DONT_SHOW_WIZARD_DIALOG_FLAG_KEY = SessionConstants.SESSION_PREFERENCES_PREFIX + "DONT_SHOW_WIZARD_DIALOG_FLAG";

    private void showGotoWizardOption() {
        CheckBox dontShowAnymoreCheckBox = new CheckBox(requireContext());
        dontShowAnymoreCheckBox.setText(R.string.dont_show_anymore);
        dontShowAnymoreCheckBox.setChecked(false);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.goto_wizard_title)
                .setView(dontShowAnymoreCheckBox)
                .setPositiveButton(R.string.answer_yes, (dialog, ignored) -> model.getDocTypes().observe(getViewLifecycleOwner(), docTypes -> {
                    Intent intent = new Intent(requireContext(), CreateDocumentWizardActivity.class);
                    intent.putStringArrayListExtra("docTypes", new ArrayList<>(docTypes));
                    startActivity(intent);
                    SessionManager.getInstance().store(ApplicationContext.get(), DONT_SHOW_WIZARD_DIALOG_FLAG_KEY, Boolean.toString(dontShowAnymoreCheckBox.isChecked()));
                }))
                .setNegativeButton(R.string.answer_no, (dialog, ignored) -> {
                    dialog.cancel();
                    SessionManager.getInstance().store(ApplicationContext.get(), DONT_SHOW_WIZARD_DIALOG_FLAG_KEY, Boolean.toString(dontShowAnymoreCheckBox.isChecked()));
                }).show().setCanceledOnTouchOutside(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        model = new ViewModelProvider(this).get(CreateDocumentModel.class);
        View root = inflater.inflate(R.layout.fragment_create_doc, container, false);
        // form init
        String isNotWizardDialogShown = SessionManager.getInstance().get(ApplicationContext.get(), DONT_SHOW_WIZARD_DIALOG_FLAG_KEY);
        if (StringUtils.isEmpty(isNotWizardDialogShown) || !Boolean.getBoolean(isNotWizardDialogShown)) {
            showGotoWizardOption();
        }
        recyclerView = root.findViewById(R.id.recyclerView);
        DataValueHashMap.init();
        initRecyclerView();
        fetchFormConfig();
        return root;
    }

    private void initRecyclerView() {
        mAdapter = new FormAdapterEx(jsonModelList, getContext(), this);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void fetchFormConfig() {
        final JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get()).orElseThrow(IllegalStateException::new);
        Async.execute(() -> {
            try {
                final GetFormConfigResponse formConfig =
                        HLFMiddlewareAPIClient.getInstance().getFormConfig(new GetFormConfigRequest(), token.toString());
                jsonModelList.addAll(fixFormConfig(formConfig.getConfig()));
                requireActivity().runOnUiThread(() -> mAdapter.notifyDataSetChanged());
            } catch (HLFException e) {
                showUserMessage(R.string.unexpected_error);
            }
        });
    }

    private <T extends JSONModel> List<T> fixFormConfig(List<T> formConfig) {
        List<String> docTypes = new ArrayList<>();
        formConfig.stream()
                .filter(model -> "doc_type_spinner".equals(model.getId()))
                .findFirst()
                .ifPresent(self -> self.getList()
                        .forEach(listItem -> {
                            String docType = HLFDataAdapter.toUserDocumentType(listItem.getIndexText());
                            docTypes.add(docType);
                            listItem.setIndexText(docType);
                        })
                );
        model.getDocTypes().postValue(docTypes);
        return formConfig;
    }

    @Override
    public void onDestroyView() {
        if (needUpdate)
            Async.execute(() -> MainActivity.getModel().getDocuments());
        super.onDestroyView();
    }

    @Override
    public void onAddAgainButtonClick() {
    }

    @Override
    public void onSubmitButtonClick() {
        if (!CheckFieldValidations.isFieldsValidated(recyclerView, jsonModelList)) {
            showUserMessage(R.string.validation_failed);
            return;
        }

        final JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get())
                .orElseThrow(IllegalStateException::new);
        Async.execute(() -> {
            try {
                List<String> signs = Arrays.asList(dataValueHashMap.get("doc_signs_multi_spinner").split(","));
                final NewDocRequest newDocRequest = NewDocRequest.builder()
                        .title(dataValueHashMap.get("doc_title_edit"))
                        .type(HLFDataAdapter.fromUserDocumentType(dataValueHashMap.get("doc_type_spinner")))
                        .owner(token.getClaim("member").asString())
                        .group(token.getClaim("group").asString())
                        .content(dataValueHashMap.get("doc_content_edit"))
                        .signsRequired(signs)
                        .build();
                HLFMiddlewareAPIClient.getInstance().newDoc(newDocRequest, token.toString());
                needUpdate = true;
                requireActivity().runOnUiThread(() -> {
                    showUserMessage(String.format(getString(R.string.doc_created_hint), dataValueHashMap.get("doc_title_edit")));
                    clearForm();
                });
            } catch (HLFException e) {
                showUserMessage(R.string.unexpected_error);
            }
        });
    }

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
}