package core.activities.ui.view_docs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.entity.Document;
import com.auth0.android.jwt.JWT;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import core.activities.R;
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.UserMessageShower;
import core.activities.ui.view_docs.model.FilterConfig;
import core.activities.ui.view_docs.model.ViewDocumentsModel;
import core.sessions.SessionConstants;
import core.sessions.SessionManager;
import core.shared.ApplicationContext;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewDocumentsFragment extends Fragment implements Traceable, UserMessageShower {
    RecyclerView recyclerView;
    ViewDocumentsModel model;
    static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();
    static String FILTER_CONFIG_STORED_KEY = SessionConstants.SESSION_PREFERENCES_PREFIX + "FILTER_CONFIG_STORED";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_docs, container, false);
        model = new ViewModelProvider(this).get(ViewDocumentsModel.class);
        // view filter pre config
        final String filterConfigJson = SessionManager.getInstance().get(ApplicationContext.get(), FILTER_CONFIG_STORED_KEY);
        FilterConfig fc = StringUtils.isNotEmpty(filterConfigJson)
                ? GSON.fromJson(filterConfigJson, FilterConfig.class) : FilterConfig.DEFAULT;
        model.getFilterConfig().setValue(fc);
        model.getFilterConfig().observe(getViewLifecycleOwner(), config -> {
            // save config in shared preferences
            SessionManager.getInstance().store(ApplicationContext.get(), FILTER_CONFIG_STORED_KEY, GSON.toJson(config));
            // reset recycler view according to config
            // TODO
            //((DocumentsViewAdapter) recyclerView.getAdapter()).docs()
        });
        // createDoc button configuring
        final FloatingActionButton createDocButton = root.findViewById(R.id.createDocButton);
        final FloatingActionButton configureFilterButton = root.findViewById(R.id.configureFilterButton);
        configureFilterButton.setOnClickListener(self -> buildAndShowConfigureFilter());
        createDocButton.setOnClickListener(self -> Navigation.findNavController(self).navigate(R.id.navCreateDoc));

        // recycler view configuring
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        final DocumentsViewAdapter adapter = new DocumentsViewAdapter();
        recyclerView.setAdapter(adapter);

        // docs binding
        ((MainActivity) Objects.requireNonNull(getActivity())).getModel().getDocsResult().observe(getViewLifecycleOwner(), getDocsResult -> {
            if (Objects.nonNull(getDocsResult.getDocuments())) {
                adapter.setDocs(filterDocs(getDocsResult.getDocuments()));
            } else if (Objects.nonNull(getDocsResult.getError())) {
                showUserMessage(getDocsResult.getError());
            }
        });
        return root;
    }

    private List<Document> filterDocs(List<Document> documents) {
        final JWT token = SessionManager.getInstance().getUserToken(requireContext())
                .orElseThrow(() -> new IllegalStateException("Token must not be null at this stage."));
        final String member = Objects.requireNonNull(token.getClaim("member")).asString();
        return documents.stream()
                // owner is logged user or doc requires sign of the user
                .filter(document -> member.equals(document.getOwner()) || document.getSignsRequired().contains(member))
                .collect(Collectors.toList());
    }

    private void buildAndShowConfigureFilter() {
        // filterConfig.isCompletedDocsShown
        final Switch completedDocsSwitch = new Switch(requireContext());
        completedDocsSwitch.setText(R.string.is_completed_docs_shown);
        completedDocsSwitch.setSwitchPadding(10);
        completedDocsSwitch.setChecked(Objects.requireNonNull(model.getFilterConfig().getValue()).isCompletedDocsShown());
        // filterConfig.isRejectedDocsShown
        final Switch rejectedDocsSwitch = new Switch(requireContext());
        rejectedDocsSwitch.setText(R.string.is_rejected_docs_shown);
        rejectedDocsSwitch.setSwitchPadding(10);
        rejectedDocsSwitch.setChecked(Objects.requireNonNull(model.getFilterConfig().getValue()).isRejectedDocsShown());
        final LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40, 40, 40, 40);
        root.addView(completedDocsSwitch, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        root.addView(rejectedDocsSwitch, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.configure_filters_title)
                .setView(root)
                .setPositiveButton(R.string.apply, (dialog, ignored) -> {
                    FilterConfig fc = new FilterConfig(completedDocsSwitch.isChecked(), rejectedDocsSwitch.isChecked());
                    model.getFilterConfig().setValue(fc);
                })
                .setNegativeButton(R.string.close, (dialog, ignored) -> dialog.cancel())
                .show().setCanceledOnTouchOutside(true);

    }
}