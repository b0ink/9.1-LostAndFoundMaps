package com.example.lostandfound;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.lostandfound.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private LostAndFoundDatabase lostAndFoundDatabase;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private Marker yourLocation;

    // center maps once location is retrieved for the first time
    private Boolean cameraCentered = false;

    public static final String EXTRA_FOCUS_LOST_ITEM_ID = "extra_focus_lost_item_id";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                // Permission denied, show a message to the user
                System.out.println("Location permission denied");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        lostAndFoundDatabase = DatabaseHelper.getInstance(this).getLostAndFoundDatabase();

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Use the latitude and longitude as needed
                System.out.println("lat/long:" + latitude + " " + longitude);
                LatLng currentLoc = new LatLng(latitude, longitude);

//                if (yourLocation != null) {
//                    yourLocation.remove();
//                    yourLocation = null;
//                }
//
//                yourLocation = mMap.addMarker(new MarkerOptions()
//                        .position(currentLoc)
//                        .title("Your location")
//                        .snippet("Your are here")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


                if (!cameraCentered) {
                    cameraCentered = true;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
                }

            }
        };


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, locationListener);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        ArrayList<LostItem> lostItems = new ArrayList<>();
        lostItems.addAll(lostAndFoundDatabase.lostItemDao().getAllLostItems());

        Intent intent = getIntent();
        int lostItemId = -1;
        if (intent != null) {
            lostItemId = intent.getIntExtra(EXTRA_FOCUS_LOST_ITEM_ID, -1);
            if (lostItemId != -1) {
                cameraCentered = true;
            }
        }

        mMap.setOnMarkerClickListener(view -> {
            try{
                int itemId = (int)view.getTag();
                if(itemId != -1){
                    startActivity(new Intent(this, ViewLostItemActivity.class).putExtra(ViewLostItemActivity.EXTRA_LOST_ITEM_ID, itemId));
                    finish();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return true;
        });

        for (LostItem item : lostItems) {
            LatLng itemLocation = new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude());
            Marker itemMarker = mMap.addMarker(new MarkerOptions()
                    .position(itemLocation)
                    .title(item.getItemName())
                    .contentDescription(item.getDescription())
                    .snippet(item.getLocation().getLocationName())
            );
            if (item.getReportType() == LostItem.REPORT_TYPE.REPORT_TYPE_FOUND) {
                itemMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                itemMarker.setTitle("Found: " + item.getItemName());
            } else {
                itemMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                itemMarker.setTitle("Lost: " + item.getItemName());
            }
            itemMarker.setTag(item.getId());

            if (item.getId() == lostItemId) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(itemLocation, 14));
            }


            item.getLocation().print();
        }


    }
}