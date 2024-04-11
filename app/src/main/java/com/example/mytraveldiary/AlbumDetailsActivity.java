package com.example.mytraveldiary;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AlbumDetailsActivity extends AppCompatActivity {

    private TextView albumNameTextView, albumDateTextView;
    private RecyclerView recyclerView;
    private PageAdapter pageAdapter;
    private Album album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        album = (Album) getIntent().getSerializableExtra("album");

        albumNameTextView = findViewById(R.id.albumNameTextView);
        albumDateTextView = findViewById(R.id.albumDateTextView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (album != null) {
            albumNameTextView.setText(album.getName());
            albumDateTextView.setText(album.getDate());

            List<Page> pages = album.getPages();
            pageAdapter = new PageAdapter(pages, new PageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Page page) {
                }
            });
            recyclerView.setAdapter(pageAdapter);
        }
    }
}
