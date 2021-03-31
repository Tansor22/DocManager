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
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsViewAdapter extends RecyclerView.Adapter<DocumentsViewHolder> {
    List<Document> _docs;

    @NonNull
    @Override
    public DocumentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.documents_view_item, parent, false);
        return new DocumentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentsViewHolder holder, int position) {
        holder.updateUI(_docs.get(position));
    }

    public void setDocs(List<Document> docs) {
        this._docs = docs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return _docs.size();
    }
}
