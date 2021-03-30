package core.activities.ui.view_docs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.entity.Document;
import core.activities.R;
import core.shared.Traceable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewDocumentsFragment extends Fragment implements Observer<List<Document>>, Traceable {
    //ViewDocumentsModel model;
    List<Document> dataset;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //model = new ViewModelProvider(this).get(ViewDocumentsModel.class);
        populateModel();
        View root = inflater.inflate(R.layout.fragment_view_docs, container, false);
        recyclerView = root.findViewById(R.id.recyclerView);
        //model.getDocs().observe(getViewLifecycleOwner(), this);
        recyclerView.setAdapter(new DocumentsViewAdapter(dataset));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        return root;
    }

    // TODO: delete
    void populateModel() {
        PodamFactory pf = new PodamFactoryImpl();
        dataset = IntStream.range(0, 20)
                .mapToObj(ignored -> pf.manufacturePojo(Document.class))
                .collect(Collectors.toList());
    }

    @Override
    public void onChanged(List<Document> documents) {
        trace("Notification received: %s", documents);
    }
}