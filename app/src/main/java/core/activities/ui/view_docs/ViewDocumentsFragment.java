package core.activities.ui.view_docs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
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
import core.activities.ui.shared.Async;
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewDocumentsFragment extends Fragment implements Traceable, UserMessageShower {
    RecyclerView recyclerView;
    ViewDocumentsModel model;

    // 3 secs
    final int docsLoadingDelay = 3_000;
    boolean docsLoadingEnabled = true;

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
            updateUI(MainActivity.getModel().getDocsResult().getValue().getDocuments());
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (docsLoadingEnabled && isEdgeReached(recyclerView, newState)) {
                    // 1. disable loading
                    docsLoadingEnabled = false;
                    // 2. show certain ui
                    final ProgressBar docLoading = root.findViewById(R.id.docLoading);
                    final TextView docLoadingText = root.findViewById(R.id.docLoadingText);
                    docLoading.setVisibility(View.VISIBLE);
                    docLoadingText.setVisibility(View.VISIBLE);
                    // 3.1 update docs async
                    Async.execute(() -> {
                        MainActivity.getModel().getDocuments();
                        // 3.2 hide ui, after docs are updated
                        requireActivity().runOnUiThread(() -> {
                            docLoading.setVisibility(View.GONE);
                            docLoadingText.setVisibility(View.GONE);
                        });
                    });
                    // 4. restrict loading to once in docsLoadingDelay
                    recyclerView.getHandler().postDelayed(() -> docsLoadingEnabled = true, docsLoadingDelay);
                }
            }
        });

        // docs binding
        MainActivity.getModel().getDocsResult().observe(getViewLifecycleOwner(), getDocsResult -> {
            if (Objects.nonNull(getDocsResult.getDocuments())) {
                updateUI(getDocsResult.getDocuments());
            } else if (Objects.nonNull(getDocsResult.getError())) {
                showUserMessage(getDocsResult.getError());
            }
        });

        return root;
    }

    private boolean isEdgeReached(RecyclerView recyclerView, int newState) {
        return newState == RecyclerView.SCROLL_STATE_IDLE // current state is idle
                && !recyclerView.canScrollVertically(-1) //  and can't scroll up
                || !recyclerView.canScrollVertically(1); // or can't scroll down
    }

    private void updateUI(List<Document> documents) {
        // 1. Filter all docs in general
        List<Document> docsToRender = filterDocs(documents);
        // 2. Filter by user's filter config
        final FilterConfig filter = model.getFilterConfig().getValue();
        docsToRender = docsToRender.stream()
                // isRejectedDocsShown = false, => hide if status = 'REJECTED'
                .filter(document -> {
                    if (!filter.isRejectedDocsShown()) {
                        return !document.getStatus().equals("REJECTED");
                    } else {
                        return true;
                    }
                })
                // isCompletedDocsShown = false, => hide if status = 'CLOSED' or 'APPROVED'
                .filter(document -> {
                    if (!filter.isCompletedDocsShown()) {
                        return !Arrays.asList("CLOSED", "APPROVED").contains(document.getStatus());
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
        // 3. render
        ((DocumentsViewAdapter) Objects.requireNonNull(recyclerView.getAdapter())).setDocs(docsToRender);
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