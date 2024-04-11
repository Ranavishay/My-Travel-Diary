package com.example.mytraveldiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AdditionalDetailsActivity extends AppCompatActivity {

    private EditText sentenceEditText;
    private EditText dateEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_details);

        sentenceEditText = findViewById(R.id.sentenceEditText);
        dateEditText = findViewById(com.google.android.material.R.id.date_picker_actions);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sentence = sentenceEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();
                // Pass details back to NewAlbumActivity
                Intent intent = new Intent();
                intent.putExtra("sentence", sentence);
                intent.putExtra("date", date);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
