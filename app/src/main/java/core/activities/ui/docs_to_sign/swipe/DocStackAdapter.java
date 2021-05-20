package core.activities.ui.docs_to_sign.swipe;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import core.activities.R;
import core.shared.ApplicationContext;

import java.util.List;

public class DocStackAdapter extends RecyclerView.Adapter<DocStackAdapter.ViewHolder> {

    private List<SwipeItemModel> items;

    public DocStackAdapter(List<SwipeItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView _title, _owner, _date, _status, _content;
        LinearLayout _signsContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            _title = itemView.findViewById(R.id.item_title);
            _owner = itemView.findViewById(R.id.item_owner);
            _date = itemView.findViewById(R.id.item_date);
            _status = itemView.findViewById(R.id.item_status);
            _content = itemView.findViewById(R.id.item_content);
            _signsContainer = itemView.findViewById(R.id.signsContainer);
        }

        void setData(SwipeItemModel model) {
            _title.setText(model.getDocument().getTitle());
            _owner.setText(ApplicationContext.get().getText(R.string.doc_owner_prefix) + " "
                    + model.getDocument().getOwner());
            _date.setText(ApplicationContext.get().getString(R.string.doc_date_prefix) + " "
                    + model.getDocument().getDateForUser());
            _status.setText(ApplicationContext.get().getString(R.string.doc_status_prefix) + " "
                    + model.getDocument().getStatusForUser());
            _content.setText(model.getDocument().getContent());
            // signs
            for (String sign : model.getDocument().getSignsRequired()) {
                TextView signTextView = new TextView(itemView.getContext());
                signTextView.setText(sign);
                signTextView.setTextColor(ContextCompat.getColor(ApplicationContext.get(), R.color.colorFullBlack));
                signTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                if (model.getDocument().getSignedBy().contains(sign)) {
                    // green check
                    signTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_approve_small, 0, 0, 0);
                } else {
                    // red cross
                    signTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_reject_small, 0, 0, 0);
                }
                _signsContainer.addView(signTextView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    public List<SwipeItemModel> getItems() {
        return items;
    }

    public void setItems(List<SwipeItemModel> items) {
        this.items = items;
    }
}
