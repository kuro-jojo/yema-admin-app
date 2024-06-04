package com.kuro.yemaadmin.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private final Context context;
    private Uri[] mImagesUri;
    private ArrayList<String> mListOfImages;

    public ImageSliderAdapter(Context context, Uri[] uris) {
        this.context = context;
        this.mImagesUri = uris;
    }

    public ImageSliderAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.mListOfImages = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create the ImageView directly in code
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return new ImageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Set the image resource for the ImageView
        if (mListOfImages != null && !mListOfImages.isEmpty()) {
            Glide.with(holder.itemView).load(mListOfImages.get(position)).into(holder.imageView);
        } else {
            holder.imageView.setImageURI(mImagesUri[position]);
        }
    }

    @Override
    public int getItemCount() {
        return (mListOfImages != null && !mListOfImages.isEmpty()) ? mListOfImages.size() : mImagesUri.length;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Cast the View to ImageView
            imageView = (ImageView) itemView;
        }
    }
}
