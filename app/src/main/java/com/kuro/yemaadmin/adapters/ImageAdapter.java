package com.kuro.yemaadmin.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kuro.yemaadmin.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private final ArrayList<Uri> mListOfImageUri;

    public ImageAdapter(ArrayList<Uri> listOfImageUri) {
        mListOfImageUri = listOfImageUri;
    }

    public ArrayList<Uri> getListOfImageUri() {
        return mListOfImageUri;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addImageUri(Uri uri) {
        this.mListOfImageUri.add(uri);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeImageUri(Uri uri) {
        this.mListOfImageUri.remove(uri);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        holder.mImage.setImageURI(mListOfImageUri.get(position));
        holder.mRemove.setOnClickListener(v -> {
            removeImageUri(mListOfImageUri.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mListOfImageUri.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImage, mRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.house_image);
            mRemove = itemView.findViewById(R.id.house_image_remove);
        }
    }
}
