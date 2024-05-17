package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class NewPostActivity extends AppCompatActivity {

    private EditText etItemName;
    private EditText etDescription;
    private EditText etLocation;
    private EditText etDate;

    private EditText etPosterName;
    private EditText etMobile;

    private Button btnSavePost;

    private TabLayout tlReportType;

    private LostAndFoundDatabase lostAndFoundDatabase;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private LocationInfo itemLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        itemLocation = new LocationInfo();

        // Initialize the Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_API_KEY);
        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        lostAndFoundDatabase = DatabaseHelper.getInstance(this).getLostAndFoundDatabase();

        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etPosterName = findViewById(R.id.etPosterName);
        etMobile = findViewById(R.id.etMobile);
        btnSavePost = findViewById(R.id.btnSavePost);
        tlReportType = findViewById(R.id.tlReportType);


        // Set up the Autocomplete intent for etLocation
        etLocation.setFocusable(false); // Prevent the keyboard from popping up
        etLocation.setOnClickListener(v -> {
            // Launch the Autocomplete intent
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(NewPostActivity.this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        });


        btnSavePost.setOnClickListener(view -> {
            LostItem.REPORT_TYPE reportType = LostItem.REPORT_TYPE.values()[tlReportType.getSelectedTabPosition()];

            String itemName = etItemName.getText().toString();
            String description = etDescription.getText().toString();
            String location = etLocation.getText().toString();
            String date = etDate.getText().toString();
            String posterName = etPosterName.getText().toString();
            String mobile = etMobile.getText().toString();

            try {
                LocalDate localDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    localDate = LocalDate.parse(date, formatter);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid date format: yyyy-MM-dd", Toast.LENGTH_SHORT).show();
                return;
            }

            if (itemName.isEmpty() || description.isEmpty() || location.isEmpty() || date.isEmpty() || posterName.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(this, "Fields cannot be left blank.", Toast.LENGTH_SHORT).show();
                return;
            }

            if(itemLocation.getLocationName().isEmpty()){
                Toast.makeText(this, "Please select a location.", Toast.LENGTH_SHORT).show();
                return;
            }

            LostItem item = new LostItem(reportType, itemName, description, itemLocation, date, posterName, mobile);
            lostAndFoundDatabase.lostItemDao().insert(item);

            Toast.makeText(this, "Created post!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ViewPostsActivity.class);
            startActivity(intent);
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                etLocation.setText(place.getName());

                itemLocation.setLocationName(place.getName());

                // You can also get the location details if needed
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;
                    itemLocation.setLatitude(latitude);
                    itemLocation.setLongitude(longitude);
                    itemLocation.print();
                    // Use latitude and longitude as needed
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                // Handle the error
                Log.i("NewPostActivity", "An error occurred: " + status.getStatusMessage());
            }
        }
    }
}