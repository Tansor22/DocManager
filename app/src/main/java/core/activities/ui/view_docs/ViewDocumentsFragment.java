package core.activities.ui.view_docs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import core.activities.R;
import core.activities.ui.main.MainActivity;
import core.activities.ui.shared.UserMessageShower;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewDocumentsFragment extends Fragment implements Traceable, UserMessageShower {
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_docs, container, false);

        // createDoc button configuring
        final FloatingActionButton createDocButton = root.findViewById(R.id.createDocButton);
        final FloatingActionButton configureFilterButton = root.findViewById(R.id.configureFilterButton);
        configureFilterButton.setOnTouchListener((self, event) -> {
            showUserMessage("Configure filter dialog opened");
            self.performClick();
            return false;
        });
        createDocButton.setOnTouchListener((self, event) -> {
            Navigation.findNavController(self).navigate(R.id.navCreateDoc);
            self.performClick();
            return true;
        });

        // recycler view configuring
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        final DocumentsViewAdapter adapter = new DocumentsViewAdapter();
        recyclerView.setAdapter(adapter);

        // docs binding
        ((MainActivity) Objects.requireNonNull(getActivity())).getModel().getDocsResult().observe(getViewLifecycleOwner(), getDocsResult -> {
            if (Objects.nonNull(getDocsResult.getDocuments())) {
                adapter.setDocs(getDocsResult.getDocuments());
            } else if (Objects.nonNull(getDocsResult.getError())) {
                showUserMessage(getDocsResult.getError());
            }
        });
        return root;
    }
}