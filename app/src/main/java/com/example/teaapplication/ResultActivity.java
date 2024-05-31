package com.example.teaapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get data from the intent
        Bitmap image = getIntent().getParcelableExtra("image");
        String resultText = getIntent().getStringExtra("result");
        String characteristicsText = getIntent().getStringExtra("characteristics");

        // Initialize views
        ImageView resultImageView = findViewById(R.id.resultImageView);
        TextView resultTextView = findViewById(R.id.resultTextView);


        // Set data to views
        resultImageView.setImageBitmap(image);
        resultTextView.setText(resultText);


        // Back button click listener
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity to go back
                finish();
            }
        });
    }
}
