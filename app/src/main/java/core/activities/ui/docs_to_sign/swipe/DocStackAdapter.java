package core.activities.ui.docs_to_sign.swipe;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import api.clients.middleware.entity.Document;
import core.activities.R;
import core.shared.ApplicationContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DocStackAdapter extends RecyclerView.Adapter<DocStackAdapter.ViewHolder> {

    private List<SwipeItemModel> items;

    public DocStackAdapter(List<SwipeItemModel> items) {
        setItems(items);
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
        final SwipeItemModel swipeItemModel = items.get(position);
        holder.setData(swipeItemModel);
        final Document document = swipeItemModel.getDocument();
        holder.configureOverlays(document);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView _title, _owner, _date, _status, _reason, _content, _rightOverlayText, _leftOverlayText;
        ImageView _rightOverlayImg, _leftOverlayImg;
        LinearLayout _signsContainer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            _title = itemView.findViewById(R.id.item_title);
            _owner = itemView.findViewById(R.id.item_owner);
            _date = itemView.findViewById(R.id.item_date);
            _status = itemView.findViewById(R.id.item_status);
            _reason = itemView.findViewById(R.id.item_reason);
            _content = itemView.findViewById(R.id.item_content);
            _signsContainer = itemView.findViewById(R.id.signsContainer);
            _rightOverlayText = itemView.findViewById(R.id.rightHint);
            _leftOverlayText = itemView.findViewById(R.id.leftHint);
            _rightOverlayImg = itemView.findViewById(R.id.rightImg);
            _leftOverlayImg = itemView.findViewById(R.id.leftImg);
        }

        void setData(SwipeItemModel model) {
            _title.setText(model.getDocument().getTitle());
            _owner.setText(ApplicationContext.get().getText(R.string.doc_owner_prefix) + " "
                    + model.getDocument().getOwner());
            _date.setText(ApplicationContext.get().getString(R.string.doc_date_prefix) + " "
                    + model.getDocument().getDateForUser());
            _status.setText(ApplicationContext.get().getString(R.string.doc_status_prefix) + " "
                    + model.getDocument().getStatusForUser());
            final String reason = model.getDocument().getLastChange().getDetails();
            if ("REJECTED".equals(model.getDocument().getStatus()) && StringUtils.isNotEmpty(reason)) {
                _reason.setVisibility(View.VISIBLE);
                _reason.setText(ApplicationContext.get().getString(R.string.doc_reject_reason) + " "
                        + reason);
            } else {
                _reason.setVisibility(View.GONE);
            }
            _content.setText(model.getDocument().getAttributes().getContent());
            // signs
            for (int i = 0; i < model.getDocument().getSignsRequired().size(); i++) {
                String sign = model.getDocument().getSignsRequired().get(i);
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

        public void configureOverlays(Document document) {
            if ("PROCESSING".equals(document.getStatus())) {
                // approve
                _rightOverlayImg.setImageResource(R.drawable.ic_approve);
                _rightOverlayText.setText(R.string.action_approve);
                _rightOverlayText.setTextColor(ApplicationContext.get().getResources().getColor(R.color.color_green));
                // reject
                _leftOverlayImg.setImageResource(R.drawable.ic_reject);
                _leftOverlayText.setText(R.string.action_reject);
                _leftOverlayText.setTextColor(ApplicationContext.get().getResources().getColor(R.color.color_red));
            } else {
                // edit
                _rightOverlayImg.setImageResource(R.drawable.ic_edit);
                _rightOverlayText.setText(R.string.action_edit);
                _rightOverlayText.setTextColor(ApplicationContext.get().getResources().getColor(R.color.grey));
                // skip
                _leftOverlayImg.setImageResource(R.drawable.ic_later);
                _leftOverlayText.setText(R.string.action_later);
                _leftOverlayText.setTextColor(ApplicationContext.get().getResources().getColor(R.color.grey));
            }
        }
    }

    public List<SwipeItemModel> getItems() {
        return items;
    }

    public void setItems(List<SwipeItemModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
