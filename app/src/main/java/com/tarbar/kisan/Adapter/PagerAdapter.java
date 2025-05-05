package com.tarbar.kisan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tarbar.kisan.R;

import java.util.List;

public class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.PagerViewHolder> {

    private final Context context;
    private final List<String> imageUrls;

    public PagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new PagerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(context).load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void addImage(String imageUrl) {
        imageUrls.add(imageUrl);
        notifyItemInserted(imageUrls.size() - 1); // Notify adapter that a new item is added
    }

    public static class PagerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PagerViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
