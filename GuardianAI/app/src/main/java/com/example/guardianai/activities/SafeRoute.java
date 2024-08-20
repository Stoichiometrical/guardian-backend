package com.example.guardianai.activities;




import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.guardianai.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SafeRoute extends AppCompatActivity implements OnMapReadyCallback {

    private static final String API_URL = "https://guardianai-m2kc.onrender.com/find_safest_route";
    private static final LatLng KENYA_CENTER = new LatLng(1.2921, 36.8219); // Coordinates of Nairobi, Kenya
    private static final float DEFAULT_ZOOM = 6.0f; // Zoom level for Kenya
    private static final String TAG = "SafeRoute"; // Tag for logging
    private TextView loadingMessage;
    private TextView displayValues;
    private GoogleMap mMap;
    private final Map<String, LatLng> townCoordinates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_route);

        // Set up toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back arrow in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title to use custom title
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize UI components
        EditText originInput = findViewById(R.id.origin_input);
        EditText destinationInput = findViewById(R.id.destination_input);
        EditText hourInput = findViewById(R.id.hour_input);
        Spinner dayOfWeekSpinner = findViewById(R.id.day_of_week_spinner);
        Button findSafestRouteButton = findViewById(R.id.find_safest_route_button);
        displayValues = findViewById(R.id.display_values);
        loadingMessage = findViewById(R.id.loading_message);

        // Set up Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(adapter);

        // Initialize town coordinates
        initializeTownCoordinates();

        // Set up Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up button click listener
        findSafestRouteButton.setOnClickListener(v -> {
            String origin = originInput.getText().toString().trim();
            String destination = destinationInput.getText().toString().trim();
            String dayOfWeekString = dayOfWeekSpinner.getSelectedItem().toString();
            String hourText = hourInput.getText().toString().trim();

            int dayOfWeek = 0;
            try {
                dayOfWeek = dayOfWeekSpinner.getSelectedItemPosition(); // Get index of selected day
            } catch (Exception e) {
                // Handle invalid day of week input
                dayOfWeek = 0; // Default to Sunday
                Log.e(TAG, "Invalid day of week input", e);
            }

            int hour = 0;
            try {
                hour = Integer.parseInt(hourText);
                if (hour < 0 || hour > 23) {
                    hour = 0; // Set to default value or handle as needed
                }
            } catch (NumberFormatException e) {
                // Handle invalid hour input
                hour = 0; // Set to default value or handle as needed
                Log.e(TAG, "Invalid hour input", e);
            }

            // Create JSON request body
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("origin", origin);
                jsonBody.put("destination", destination);
                jsonBody.put("day_of_week", dayOfWeek);
                jsonBody.put("hour_of_day", hour);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Error creating JSON request body", e);
            }

            loadingMessage.setVisibility(View.VISIBLE);

            // Increase timeout settings
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Increase connection timeout
                    .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // Increase write timeout
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)  // Increase read timeout
                    .build();

            RequestBody body = RequestBody.create(jsonBody.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        loadingMessage.setVisibility(View.GONE);
                        Toast.makeText(SafeRoute.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "API request failed", e);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        loadingMessage.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                JSONArray routeDescriptionArray = jsonResponse.getJSONArray("route_description");
                                JSONArray safestPathArray = jsonResponse.getJSONArray("safest_path");

                                // Concatenate all route descriptions
                                StringBuilder routeDescription = new StringBuilder();
                                for (int i = 0; i < routeDescriptionArray.length(); i++) {
                                    routeDescription.append(routeDescriptionArray.optString(i)).append("\n");
                                }

                                // Parse route description
//                                String routeDescription = routeDescriptionArray.length() > 0 ? routeDescriptionArray.getString(0) : "No description available";

                                // Parse safest path
                                if (mMap != null) {
                                    plotRouteOnMap(safestPathArray);
                                }

                                displayValues.setVisibility(View.VISIBLE);
                                displayValues.setText("Route Description: " + routeDescription);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Error parsing JSON response", e);
                                displayValues.setVisibility(View.VISIBLE);
                                displayValues.setText("Error parsing response");
                            }
                        } else {
                            Log.e(TAG, "API response failed: " + response.message());
                            displayValues.setVisibility(View.VISIBLE);
                            displayValues.setText("Request failed: " + response.message());
                        }
                    });
                }
            });
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Center the map around Kenya
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KENYA_CENTER, DEFAULT_ZOOM));
    }

    private void initializeTownCoordinates() {
        // Initialize town coordinates here
        townCoordinates.put("Nairobi", new LatLng(-1.286389, 36.817223));
        townCoordinates.put("Mombasa", new LatLng(-4.043477, 39.668206));
        townCoordinates.put("Nakuru", new LatLng(-0.303099, 36.066285));
        townCoordinates.put("Eldoret", new LatLng(0.514277, 35.269065));
        townCoordinates.put("Kisumu", new LatLng(-0.091702, 34.767956));
        townCoordinates.put("Nyeri", new LatLng(-0.421250, 36.945752));
        townCoordinates.put("Machakos", new LatLng(-1.516820, 37.266485));
        townCoordinates.put("Kericho", new LatLng(-0.366690, 35.291340));
        townCoordinates.put("Meru", new LatLng(-0.047200, 37.648000));
        townCoordinates.put("Kitale", new LatLng(1.002559, 34.986032));
        townCoordinates.put("Garissa", new LatLng(-0.456550, 39.664640));
        townCoordinates.put("Isiolo", new LatLng(0.353073, 37.582666));
        townCoordinates.put("Bungoma", new LatLng(0.591278, 34.564658));
        townCoordinates.put("Wajir", new LatLng(1.737327, 40.065940));
        townCoordinates.put("Mandera", new LatLng(3.930530, 41.855910));
        townCoordinates.put("Malindi", new LatLng(-3.219186, 40.116944));
        townCoordinates.put("Lamu", new LatLng(-2.271189, 40.902012));
        townCoordinates.put("Thika", new LatLng(-1.033349, 37.069328));
        townCoordinates.put("Namanga", new LatLng(-2.545290, 36.792530));
        townCoordinates.put("Kitui", new LatLng(-1.374818, 38.010555));
        townCoordinates.put("Naivasha", new LatLng(-0.707222, 36.431944));
        townCoordinates.put("Narok", new LatLng(-1.078850, 35.860000));
        townCoordinates.put("Busia", new LatLng(0.4605, 34.1115));
        townCoordinates.put("Bomet", new LatLng(-0.7827, 35.3428));
        townCoordinates.put("Marsabit", new LatLng(2.3342, 37.9891));
        townCoordinates.put("Voi", new LatLng(-3.3962, 38.5565));
    }

    private void plotRouteOnMap(JSONArray safestPathArray) {
        if (safestPathArray.length() < 2) {
            Log.e(TAG, "Insufficient data to plot route");
            return;
        }

        PolylineOptions polylineOptions = new PolylineOptions().color(getResources().getColor(R.color.button_color)).width(5);

        for (int i = 0; i < safestPathArray.length(); i++) {
            String townName = safestPathArray.optString(i);
            LatLng coordinates = townCoordinates.get(townName);
            if (coordinates != null) {
                polylineOptions.add(coordinates);
            } else {
                Log.e(TAG, "Town coordinates not found for: " + townName);
            }
        }

        if (mMap != null) {
            mMap.clear(); // Clear previous markers and routes
            mMap.addPolyline(polylineOptions);

            // Zoom to fit the polyline
            if (polylineOptions.getPoints().size() > 0) {
                LatLng firstPoint = polylineOptions.getPoints().get(0);
                LatLng lastPoint = polylineOptions.getPoints().get(polylineOptions.getPoints().size() - 1);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(firstPoint);
                builder.include(lastPoint);
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }
    }
}



//package com.example.guardianai.activities;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.guardianai.R;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//public class SafeRoute extends AppCompatActivity implements OnMapReadyCallback {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_safe_route);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Set up the map
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//
//        // Set up the Spinner
//        Spinner dayOfWeekSpinner = findViewById(R.id.day_of_week_spinner);
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.days_of_week, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dayOfWeekSpinner.setAdapter(adapter);
//
//        // Initialize other UI components
//        EditText originInput = findViewById(R.id.origin_input);
//        EditText destinationInput = findViewById(R.id.destination_input);
//        EditText hourInput = findViewById(R.id.hour_input);
//        Button findSafestRouteButton = findViewById(R.id.find_safest_route_button);
//        TextView displayValues = findViewById(R.id.display_values);
//
//        // Set up Spinner item selection listener
//        dayOfWeekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedDay = parent.getItemAtPosition(position).toString();
//                Log.d("Spinner", "Selected Day: " + selectedDay);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Handle the case when nothing is selected, if needed
//            }
//        });
//
//        // Set up the button click listener
//        findSafestRouteButton.setOnClickListener(v -> {
//            String origin = originInput.getText().toString();
//            String destination = destinationInput.getText().toString();
//            String dayOfWeek = dayOfWeekSpinner.getSelectedItem().toString();
//            String hourText = hourInput.getText().toString();
//
//            int hour = 0;
//            try {
//                hour = Integer.parseInt(hourText);
//                if (hour < 0 || hour > 23) {
//                    hour = 0; // Set to default value or handle as needed
//                }
//            } catch (NumberFormatException e) {
//                // Handle invalid hour input
//                hour = 0; // Set to default value or handle as needed
//            }
//
//            // Display the values and make TextView visible
//            String displayText = "Point of Origin: " + origin + "\n" +
//                    "Destination: " + destination + "\n" +
//                    "Day of Week: " + dayOfWeek + "\n" +
//                    "Hour of Day: " + hour;
//
//            displayValues.setText(displayText);
//            displayValues.setVisibility(View.VISIBLE);
//        });
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        // Center the map on Kenya
//        LatLng kenya = new LatLng(-1.2921, 36.8219); // Coordinates for Nairobi, Kenya
//        googleMap.addMarker(new MarkerOptions().position(kenya).title("Nairobi, Kenya"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kenya, 6.0f)); // Zoom level 6
//    }
//}

