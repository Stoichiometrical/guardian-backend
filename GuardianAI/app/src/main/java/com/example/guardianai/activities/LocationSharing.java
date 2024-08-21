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


//package com.example.guardianai.activities;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.animation.AlphaAnimation;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
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
//    private LatLng currentLocation;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_location_sharing);
//
//        // Set up the toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Live Location Sharing");
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        ImageView micIcon = findViewById(R.id.mic_icon);
//        TextView recordingControlText = findViewById(R.id.recording_control_text);
//        Button shareLocationButton = findViewById(R.id.distress_signal_button);
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
//
//        // Set up the share location button
//        shareLocationButton.setOnClickListener(v -> shareLiveLocation());
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
//                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
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
//
//    private void shareLiveLocation() {
//        if (currentLocation != null) {
//            String locationUri = "http://maps.google.com/maps?q=loc:" + currentLocation.latitude + "," + currentLocation.longitude;
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is my live location, I think I might be unsafe where I am going: " + locationUri);
//            startActivity(Intent.createChooser(shareIntent, "Share Location Using"));
//        } else {
//            Toast.makeText(this, "Location is not available. Please try again later.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            // Navigate back to the main activity
//            Intent intent = new Intent(LocationSharing.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}

package com.example.guardianai.activities;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocationSharing extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean isSendingSignal = false;
    private AlphaAnimation throbbingAnimation;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private ExecutorService executorService;
    private static final String TAG = "LocationSharing";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_sharing);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Live Location Sharing");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
                sendDistressSignal();
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

        // Set up bottom navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Intent intent = null;

            if (item.getItemId() == R.id.navigation_live_location) {
                intent = new Intent(LocationSharing.this, LocationSharing.class);
            } else if (item.getItemId() == R.id.navigation_safe_route) {
                intent = new Intent(LocationSharing.this, SafeRoute.class);
            } else if (item.getItemId() == R.id.navigation_recording) {
                intent = new Intent(LocationSharing.this, Recording.class);
            } else if (item.getItemId() == R.id.navigation_profile) {
                intent = new Intent(LocationSharing.this, Profile.class);
            }

            if (intent != null) {
                startActivity(intent);
                // Apply transition animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            return true; // Return true to indicate the item selection was handled
        });
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

    private void shareLiveLocation() {
        if (currentLocation != null) {
            // Share the current location as a live location
            String locationUrl = "http://maps.google.com/maps?q=" + currentLocation.latitude + "," + currentLocation.longitude;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "I'm here: " + locationUrl);
            startActivity(Intent.createChooser(shareIntent, "Share location via"));
        } else {
            Toast.makeText(this, "Location not found. Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDistressSignal() {
        if (currentLocation != null) {
            String distressMessage = "Hello, this is my distress signal. I might be in danger, here is my current location: http://maps.google.com/maps?q=" + currentLocation.latitude + "," + currentLocation.longitude;
            executorService.execute(() -> {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("https://guardianai-m2kc.onrender.com/send-alert");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("description", distressMessage);

                    Log.d(TAG, "Sending distress signal: " + jsonParam.toString()); // Log the payload

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = connection.getResponseCode();
                    Log.d(TAG, "Response Code: " + responseCode); // Log the response code

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Log.d(TAG, "Response Body: " + response.toString()); // Log the response body

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        runOnUiThread(() -> Toast.makeText(LocationSharing.this, "Distress signal sent successfully.", Toast.LENGTH_SHORT).show());
                    } else {
                        runOnUiThread(() -> Toast.makeText(LocationSharing.this, "Failed to send distress signal.", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error sending distress signal: " + e.getMessage(), e); // Log the error
                    runOnUiThread(() -> Toast.makeText(LocationSharing.this, "Error sending distress signal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Location not available. Try again later.", Toast.LENGTH_SHORT).show();
        }
    }

}


