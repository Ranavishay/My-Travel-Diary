package com.example.mytraveldiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.provider.DocumentsContract;


public class NewAlbumActivity extends AppCompatActivity {

    private EditText albumNameEditText,albumDateEditText;
    private static final int REQUEST_FILE_PICKER = 1;
    private DatabaseReference databaseRef;
    private List<Page> pages;
    private static final int REQUEST_ADDITIONAL_DETAILS = 2;
    private String filePath,fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_album);

        albumNameEditText = findViewById(R.id.albumNameEditText);
        albumDateEditText = findViewById(com.google.android.material.R.id.date_picker_actions);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        pages = new ArrayList<>();

        Button addPageButton = findViewById(R.id.addPageButton);
        addPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        Button saveAlbumButton = findViewById(R.id.saveAlbumButton);
        saveAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String albumName = albumNameEditText.getText().toString().trim();
                String albumDate = albumDateEditText.getText().toString().trim();
                if (!albumName.isEmpty()) {
                    if (pages.isEmpty()) {
                        Toast.makeText(NewAlbumActivity.this, "Please add at least one page", Toast.LENGTH_SHORT).show();
                    } else {
                        checkAlbumNameExists(albumName,albumDate);
                    }
                } else {
                    Toast.makeText(NewAlbumActivity.this, "Please enter an album name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_FILE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_PICKER && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            filePath = getFilePathFromUri(this, uri);
            fileType = "";
            if (filePath != null && !filePath.isEmpty()) {
                int lastDotIndex = filePath.lastIndexOf('.');
                if (lastDotIndex != -1) {
                    fileType = getFileType(filePath.substring(lastDotIndex + 1));
                }
            }
            // Open the AdditionalDetailsActivity and pass file details
            Intent intent = new Intent(this, AdditionalDetailsActivity.class);
            startActivityForResult(intent, REQUEST_ADDITIONAL_DETAILS);
        } else if (requestCode == REQUEST_ADDITIONAL_DETAILS && resultCode == RESULT_OK && data != null) {
            // Get page details from AdditionalDetailsActivity
            String sentence = data.getStringExtra("sentence");
            String date = data.getStringExtra("date");
            Page page = new Page(filePath,fileType, sentence, date);
            pages.add(page);
            Toast.makeText(this, "Page added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileType(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
                return "image";
            case "mp4":
            case "avi":
            case "mkv":
                return "video";
            default:
                return "unknown"; // Unrecognized files
        }
    }
    private String getFilePathFromUri(Context context, Uri uri) {
        String realPath = null;
        if (uri == null) {
            return null;
        }

        // Check if the URI is "content"
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    realPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};

                    try (Cursor cursor = context.getContentResolver().query(contentUri, null, selection, selectionArgs, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            realPath = cursor.getString(columnIndex);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            realPath = uri.getPath();
        }
        return realPath;
    }


    private void checkAlbumNameExists(final String albumName,final String albumDate) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseRef.child("albums").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String existingAlbumName = snapshot.getKey();
                            if (existingAlbumName != null && existingAlbumName.equals(albumName)) {
                                Toast.makeText(NewAlbumActivity.this, "Album name already in use", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    saveAlbum(albumName,albumDate);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(NewAlbumActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAlbum(String albumName,String albumDate) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            //String date = getCurrentDateTime();

            DatabaseReference albumRef = databaseRef.child("albums").child(userId).child(albumName);
            Map<String, Object> albumData = new HashMap<>();
            albumData.put("name", albumName);
            albumData.put("date", albumDate);

            albumRef.setValue(albumData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        DatabaseReference pagesRef = albumRef.child("pages");
                        for (int i = 0; i < pages.size(); i++) {
                            Page page = pages.get(i);
                            String pageId = String.valueOf(i);
                            Map<String, Object> pageData = new HashMap<>();
                            pageData.put("fileType", page.getFileType());
                            pageData.put("filePath", page.getFilePath());
                            pageData.put("sentence", page.getSentence());
                            pageData.put("date", page.getDate());

                            pagesRef.child(pageId).setValue(pageData, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Toast.makeText(NewAlbumActivity.this, "Album saved successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(NewAlbumActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(NewAlbumActivity.this, "Failed to save page: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(NewAlbumActivity.this, "Failed to save album: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    /*private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }*/
}