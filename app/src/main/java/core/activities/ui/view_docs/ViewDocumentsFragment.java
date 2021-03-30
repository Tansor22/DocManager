package core.activities.ui.view_docs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import core.activities.R;

public class ViewDocumentsFragment extends Fragment {

    private ViewDocumentsModel viewDocumentsModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewDocumentsModel = new ViewModelProvider(this).get(ViewDocumentsModel.class);
        View root = inflater.inflate(R.layout.fragment_view_docs, container, false);
        final TextView textView = root.findViewById(R.id.text_view_docs);
        viewDocumentsModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
}