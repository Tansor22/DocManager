package core.activities.ui.view_docs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import core.activities.R;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewDocumentsFragment extends Fragment implements Traceable {
    ViewDocumentsModel model;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_docs, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        final DocumentsViewAdapter adapter = new DocumentsViewAdapter();
        recyclerView.setAdapter(adapter);
        model = new ViewModelProvider(this).get(ViewDocumentsModel.class);
        model.getDocs().observe(getViewLifecycleOwner(), adapter::setDocs);
        return root;
    }
}