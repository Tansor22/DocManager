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
import core.activities.R;
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.Async;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.shared.forms.CheckFieldValidations;
import core.activities.ui.shared.forms.FormAdapterEx;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;

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
                jsonModelList.addAll(formConfig.getConfig());
                requireActivity().runOnUiThread(() -> mAdapter.notifyDataSetChanged());
            } catch (HLFException e) {
                showUserMessage(R.string.unexpected_error);
            }
        });
    }

    @Override
    public void onDestroyView() {
        // todo unreliable
        if (needUpdate)
            Async.execute(() -> ((MainActivity) requireActivity()).getModel().getDocuments());
        super.onDestroyView();
    }

    @Override
    public void onAddAgainButtonClick() {
    }

    @Override
    public void onSubmitButtonClick() {
        if (!CheckFieldValidations.isFieldsValidated(recyclerView, jsonModelList)) {
            Toast.makeText(requireContext(), R.string.validation_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        final JWT token = SessionManager.getInstance().getUserToken(ApplicationContext.get())
                .orElseThrow(IllegalStateException::new);
        Async.execute(() -> {
            try {
                List<String> signs = Arrays.asList(dataValueHashMap.get("doc_signs_multi_spinner").split(","));
                final NewDocRequest newDocRequest = NewDocRequest.builder()
                        .title(dataValueHashMap.get("doc_title_edit"))
                        .type(dataValueHashMap.get("doc_type_spinner"))
                        .owner(token.getClaim("member").asString())
                        .group(token.getClaim("group").asString())
                        .content(dataValueHashMap.get("doc_content_edit"))
                        .signsRequired(signs)
                        .build();
                HLFMiddlewareAPIClient.getInstance().newDoc(newDocRequest, token.toString());
                needUpdate = true;
            } catch (HLFException e) {
                showUserMessage(R.string.unexpected_error);
            }
        });
    }
}