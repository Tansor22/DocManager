package core.activities.ui.doc_details.swipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import core.activities.R;

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

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView nama, usia, kota;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            nama = itemView.findViewById(R.id.item_title);
            usia = itemView.findViewById(R.id.item_date);
            kota = itemView.findViewById(R.id.item_status);
        }

        void setData(SwipeItemModel data) {
            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(image);
            nama.setText(data.getNama());
            usia.setText(data.getUsia());
            kota.setText(data.getKota());
        }
    }

    public List<SwipeItemModel> getItems() {
        return items;
    }

    public void setItems(List<SwipeItemModel> items) {
        this.items = items;
    }
}
