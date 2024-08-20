//package com.example.guardianai.activities;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.view.animation.AlphaAnimation;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.guardianai.R;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//public class LocationSharing extends AppCompatActivity implements OnMapReadyCallback {
//
//    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
//    private boolean isSendingSignal = false;
//    private AlphaAnimation throbbingAnimation;
//    private GoogleMap mMap;
//    private FusedLocationProviderClient fusedLocationClient;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_location_sharing);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        ImageView micIcon = findViewById(R.id.mic_icon);
//        TextView recordingControlText = findViewById(R.id.recording_control_text);
//
//        // Initialize throbbing animation
//        throbbingAnimation = new AlphaAnimation(1.0f, 0.5f);
//        throbbingAnimation.setDuration(500);
//        throbbingAnimation.setRepeatCount(AlphaAnimation.INFINITE);
//        throbbingAnimation.setRepeatMode(AlphaAnimation.REVERSE);
//
//        micIcon.setOnClickListener(v -> {
//            if (!isSendingSignal) {
//                // Start sending distress signal
//                micIcon.startAnimation(throbbingAnimation);
//                recordingControlText.setText("Sending distress signal...");
//                Toast.makeText(LocationSharing.this, "Distress signal sent, your location is shared, and recording started", Toast.LENGTH_LONG).show();
//            } else {
//                // Stop sending distress signal
//                micIcon.clearAnimation();
//                recordingControlText.setText("Stopped sending signal");
//                Toast.makeText(LocationSharing.this, "Stopped sending distress signal", Toast.LENGTH_SHORT).show();
//            }
//            isSendingSignal = !isSendingSignal;
//        });
//
//        // Initialize the map
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map_container);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//
//        // Initialize fusedLocationClient
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        mMap = googleMap;
//        enableMyLocation();
//    }
//
//    private void enableMyLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        // Get the user's current location and move the map's camera to it
//                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
//                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
//                    }
//                });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                enableMyLocation();
//            } else {
//                Toast.makeText(this, "Location permission required to show your position on the map", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//}


package com.example.guardianai.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.guardianai.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationSharing extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean isSendingSignal = false;
    private AlphaAnimation throbbingAnimation;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_sharing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView micIcon = findViewById(R.id.mic_icon);
        TextView recordingControlText = findViewById(R.id.recording_control_text);
        Button shareLocationButton = findViewById(R.id.distress_signal_button);

        // Initialize throbbing animation
        throbbingAnimation = new AlphaAnimation(1.0f, 0.5f);
        throbbingAnimation.setDuration(500);
        throbbingAnimation.setRepeatCount(AlphaAnimation.INFINITE);
        throbbingAnimation.setRepeatMode(AlphaAnimation.REVERSE);

        micIcon.setOnClickListener(v -> {
            if (!isSendingSignal) {
                // Start sending distress signal
                micIcon.startAnimation(throbbingAnimation);
                recordingControlText.setText("Sending distress signal...");
                Toast.makeText(LocationSharing.this, "Distress signal sent, your location is shared, and recording started", Toast.LENGTH_LONG).show();
            } else {
                // Stop sending distress signal
                micIcon.clearAnimation();
                recordingControlText.setText("Stopped sending signal");
                Toast.makeText(LocationSharing.this, "Stopped sending distress signal", Toast.LENGTH_SHORT).show();
            }
            isSendingSignal = !isSendingSignal;
        });

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the share location button
        shareLocationButton.setOnClickListener(v -> shareLiveLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Get the user's current location and move the map's camera to it
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                        mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission required to show your position on the map", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareLiveLocation() {
        if (currentLocation != null) {
            String locationUri = "http://maps.google.com/maps?q=loc:" + currentLocation.latitude + "," + currentLocation.longitude;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is my live location,l think l might be unsafe where l am going: " + locationUri);
            startActivity(Intent.createChooser(shareIntent, "Share Location Using"));
        } else {
            Toast.makeText(this, "Location is not available. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
}
