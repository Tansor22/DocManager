package core.activities.ui.create_doc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import core.activities.R;

public class CreateDocumentFragment extends Fragment {

    private CreateDocumentModel createDocumentModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createDocumentModel = new ViewModelProvider(this).get(CreateDocumentModel.class);
        View root = inflater.inflate(R.layout.fragment_create_doc, container, false);
        final TextView textView = root.findViewById(R.id.text_create_doc);
        createDocumentModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}