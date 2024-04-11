package com.example.mytraveldiary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albums;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Album album);
    }

    public AlbumAdapter(List<Album> albums, OnItemClickListener listener) {
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        final Album album = albums.get(position);
        holder.albumNameTextView.setText(album.getName());
        holder.albumDateTextView.setText(album.getDate());

        // Load the first image from the album using Glide
        if (!album.getPages().isEmpty()) {
            Page firstPage = album.getPages().get(0);
            String filePath = firstPage.getFilePath();
            Context context = holder.itemView.getContext();
            Glide.with(context)
                    .load(filePath)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.albumBackgroundImageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(album);
            }
        });
    }


    @Override
    public int getItemCount() {

        return albums.size();
    }

    /*public String getFilePathFromContentUri(Context context, Uri contentUri) {
        String filePath = null;
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }*/

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView albumNameTextView;
        TextView albumDateTextView;
        ImageView albumBackgroundImageView;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameTextView = itemView.findViewById(R.id.albumNameTextView);
            albumDateTextView = itemView.findViewById(R.id.albumDateTextView);
            albumBackgroundImageView = itemView.findViewById(R.id.albumBackgroundImageView);
        }
    }
}
