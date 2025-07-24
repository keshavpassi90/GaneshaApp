package com.ganeshji.app.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ganeshji.app.R;
import com.ganeshji.app.retrofit.wallpaper_model.PhotosItem;

import java.util.List;

public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder> {

    private final Context context;
    private final List<PhotosItem> imageUrls;
    private final OnItemClickListener listener;

    // Define interface
    public interface OnItemClickListener {
        void onItemClick(String imageUrl, int position,boolean isDownload);
    }

    // Constructor with click listener
    public ImageGridAdapter(Context context, List<PhotosItem> imageUrls, OnItemClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        PhotosItem makeText = imageUrls.get(position);
        Uri uri = Uri.parse(makeText.getSrc().getOriginal());
        Log.e("Data","URL == "+makeText.getSrc().getOriginal());
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setOldController(holder.imageView.getController())
                .build();

        holder.imageView.setController(controller);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(makeText.getSrc().getOriginal(), position,false);
            }
        });
        holder.downloadIcon.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(makeText.getSrc().getOriginal(), position,true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView imageView;
        ImageView downloadIcon;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            downloadIcon = itemView.findViewById(R.id.downloadIcon);
        }
    }
}
