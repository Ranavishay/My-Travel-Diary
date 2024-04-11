package com.example.mytraveldiary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.io.File;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

    private List<Page> pages;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(Page page);
    }

    public PageAdapter(List<Page> pages, OnItemClickListener listener) {
        this.pages = pages;
        this.listener = listener;
    }


    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        Page page = pages.get(position);
        holder.bind(page, listener);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    //Create PageViewHolder that extends RecyclerView for each item_page.xml
    public class PageViewHolder extends RecyclerView.ViewHolder {

        private TextView sentenceTextView;
        private TextView dateTextView;
        private ImageView pageImageView;
        private VideoView pageVideoView;

        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentenceTextView = itemView.findViewById(R.id.sentenceTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            pageImageView = itemView.findViewById(R.id.pageImageView);
            pageVideoView = itemView.findViewById(R.id.pageVideoView);
        }

        public void bind(final Page page, final OnItemClickListener listener) {
            sentenceTextView.setText(page.getSentence());
            dateTextView.setText(page.getDate());

            String fileType = page.getFileType();
            String filePath = page.getFilePath();

            if (fileType != null && filePath != null) {
                if (fileType.equals("image")) {
                    pageImageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load(filePath).into(pageImageView);
                    pageVideoView.setVisibility(View.GONE);
                } else if (fileType.equals("video")) {
                    pageVideoView.setVisibility(View.VISIBLE);
                    MediaController mediaController = new MediaController(context);
                    pageVideoView.setVideoPath(filePath);
                    mediaController.setAnchorView(pageVideoView);
                    pageVideoView.setMediaController(mediaController);
                    pageVideoView.start();
                    pageImageView.setVisibility(View.GONE);
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(page);
                }
            });
        }
    }

}
