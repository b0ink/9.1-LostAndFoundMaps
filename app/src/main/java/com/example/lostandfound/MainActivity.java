package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnNewPost;
    private Button btnViewPosts;
    private Button btnViewMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnNewPost = findViewById(R.id.btnNewPost);
        btnViewPosts = findViewById(R.id.btnViewPosts);
        btnViewMap = findViewById(R.id.btnViewMap);

        btnNewPost.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewPostActivity.class);
            startActivity(intent);
        });

        btnViewPosts.setOnClickListener(view -> {
            Intent intent = new Intent(this, ViewPostsActivity.class);
            startActivity(intent);
        });

        btnViewMap.setOnClickListener(view -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });
    }
}