package com.example.cameragall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private Context context;
    private List<String> photoPaths;

    public PhotoAdapter(Context context, List<String> photoPaths) {
        this.context = context;
        this.photoPaths = photoPaths;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String photoPath = photoPaths.get(position);
        Glide.with(context)
                .load(photoPath)
                .into(holder.imageViewPhoto);
    }

    @Override
    public int getItemCount() {
        return photoPaths.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPhoto = itemView.findViewById(R.id.imageViewPhoto);
        }
    }
}
