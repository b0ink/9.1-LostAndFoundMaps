package com.example.lostandfound;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewLostItemActivity extends AppCompatActivity {

    public static final String EXTRA_LOST_ITEM_ID = "extra_lost_item_id";

    private TextView tvReportTypeTitle;
    private TextView tvItemName;
    private TextView tvDescription;
    private TextView tvDateReported;
    private TextView tvLocation;
    private TextView tvContactInformation;

    private Button btnViewAllPosts;
    private Button btnRemovePost;

    private LostAndFoundDatabase lostAndFoundDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_lost_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvReportTypeTitle = findViewById(R.id.tvReportType_Title);
        tvItemName = findViewById(R.id.tvItemName);
        tvDescription = findViewById(R.id.tvDescription);
        tvDateReported = findViewById(R.id.tvDateReported);
        tvLocation = findViewById(R.id.tvLocation);
        tvContactInformation = findViewById(R.id.tvContactInformation);

        btnViewAllPosts = findViewById(R.id.btnViewAllPosts);
        btnRemovePost = findViewById(R.id.btnRemovePost);

        Intent intent = getIntent();
        if (!intent.hasExtra(EXTRA_LOST_ITEM_ID)) {
            goBackToAllPosts();
            return;
        }

        int lostItemId = intent.getIntExtra(EXTRA_LOST_ITEM_ID, -1);
        if (lostItemId == -1) {
            goBackToAllPosts();
            return;
        }

        lostAndFoundDatabase = DatabaseHelper.getInstance(this).getLostAndFoundDatabase();
        LostItem lostItem = lostAndFoundDatabase.lostItemDao().getLostItemById(lostItemId);

        if (lostItem == null) {
            goBackToAllPosts();
            return;
        }

        btnViewAllPosts.setOnClickListener(view -> {
            goBackToAllPosts();
        });

        btnRemovePost.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lostAndFoundDatabase.lostItemDao().delete(lostItem);
                            goBackToAllPosts();
                            Toast.makeText(ViewLostItemActivity.this, "Successfully deleted post", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        });

        if (lostItem.getReportType() == LostItem.REPORT_TYPE.REPORT_TYPE_LOST) {
            tvReportTypeTitle.setText("Missing item");
        } else {
            tvReportTypeTitle.setText("Found item");
        }

        tvItemName.setText(lostItem.getItemName());
        tvDescription.setText(lostItem.getDescription());
        tvDateReported.setText(lostItem.formatTimeAgo());
        tvLocation.setText(lostItem.getLocation().getLocationName());
        tvContactInformation.setText(lostItem.getPostersName() + "\n" + lostItem.getMobile());


    }

    private void goBackToAllPosts() {
        Intent homeIntent = new Intent(this, ViewPostsActivity.class);
        startActivity(homeIntent);
        finish();
    }
}