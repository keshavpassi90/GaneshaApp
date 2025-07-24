package com.ganeshji.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ganeshji.app.R;
import com.ganeshji.app.retrofit.wallpaper_model.GaneshItem;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(GaneshItem item, int position);
    }

    private final Context context;
    private final ArrayList<GaneshItem> items;
    private final FavouriteAdapter.OnItemClickListener clickListener;   // NEW

    public FavouriteAdapter(Context context,
                             ArrayList<GaneshItem> items,
                            FavouriteAdapter.OnItemClickListener listener) {
        this.context       = context;
        this.items         = items;
        this.clickListener = listener;
    }

    // ------------ ViewHolder ------------
    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV, descriptionTV;
        ImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);
            titleTV       = itemView.findViewById(R.id.titleTV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            imageView     = itemView.findViewById(R.id.imageView);
        }
    }

    // ------------ Adapter overrides ------------
    @NonNull
    @Override
    public FavouriteAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_favourites, parent, false);
        return new FavouriteAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.ItemViewHolder h, int pos) {
        GaneshItem item = items.get(pos);

        h.titleTV.setText(item.getTitle());
        h.descriptionTV.setText(item.getDescription());
        h.imageView.setImageResource(item.getIconData());
        h.imageView.clearColorFilter();

        // click callback
        h.itemView.setOnClickListener(v ->
                clickListener.onItemClick(item, h.getBindingAdapterPosition()));
    }

    @Override public int getItemCount() { return items.size(); }
}


