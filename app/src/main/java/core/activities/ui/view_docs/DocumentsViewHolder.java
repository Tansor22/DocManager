package core.activities.ui.view_docs;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.entity.Document;
import core.activities.R;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Getter
@Setter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Accessors(fluent = true, prefix = "_")
public class DocumentsViewHolder extends RecyclerView.ViewHolder {
    TextView _view;
    @NonFinal
    Document _doc;
    public DocumentsViewHolder(@NonNull View itemView) {
        super(itemView);
        _view = itemView.findViewById(R.id.textView);
    }
}
