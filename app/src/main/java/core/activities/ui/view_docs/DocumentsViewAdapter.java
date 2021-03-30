package core.activities.ui.view_docs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.entity.Document;
import core.activities.R;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class DocumentsViewAdapter extends RecyclerView.Adapter<DocumentsViewHolder> {
    List<Document> _dataset;

    @NonNull
    @Override
    public DocumentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.documents_view_item, parent, false);
        return new DocumentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentsViewHolder holder, int position) {
        holder.view().setText("Doc with id " + _dataset.get(position).getDocumentId());
    }

    @Override
    public int getItemCount() {
        return _dataset.size();
    }
}
