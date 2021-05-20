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

@Getter
@Setter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Accessors(fluent = true, prefix = "_")
public class DocumentsViewHolder extends RecyclerView.ViewHolder {
    TextView _date;
    TextView _title;
    TextView _status;

    public DocumentsViewHolder(@NonNull View itemView) {
        super(itemView);
        _date = itemView.findViewById(R.id.tvDate);
        _title = itemView.findViewById(R.id.tvTitle);
        _status = itemView.findViewById(R.id.tvStatus);
    }

    void updateUI(Document doc) {
        _date.setText(doc.getDateForUser());
        _title.setText(doc.getTitle());
        _status.setText(doc.getStatusForUser());
    }
}
