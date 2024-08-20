package com.example.guardianai.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.guardianai.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SafeRoute extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_route);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up the Spinner
        Spinner dayOfWeekSpinner = findViewById(R.id.day_of_week_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.days_of_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(adapter);

        // Initialize other UI components
        EditText originInput = findViewById(R.id.origin_input);
        EditText destinationInput = findViewById(R.id.destination_input);
        EditText hourInput = findViewById(R.id.hour_input);
        Button findSafestRouteButton = findViewById(R.id.find_safest_route_button);
        TextView displayValues = findViewById(R.id.display_values);

        // Set up Spinner item selection listener
        dayOfWeekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = parent.getItemAtPosition(position).toString();
                Log.d("Spinner", "Selected Day: " + selectedDay);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected, if needed
            }
        });

        // Set up the button click listener
        findSafestRouteButton.setOnClickListener(v -> {
            String origin = originInput.getText().toString();
            String destination = destinationInput.getText().toString();
            String dayOfWeek = dayOfWeekSpinner.getSelectedItem().toString();
            String hourText = hourInput.getText().toString();

            int hour = 0;
            try {
                hour = Integer.parseInt(hourText);
                if (hour < 0 || hour > 23) {
                    hour = 0; // Set to default value or handle as needed
                }
            } catch (NumberFormatException e) {
                // Handle invalid hour input
                hour = 0; // Set to default value or handle as needed
            }

            // Display the values and make TextView visible
            String displayText = "Point of Origin: " + origin + "\n" +
                    "Destination: " + destination + "\n" +
                    "Day of Week: " + dayOfWeek + "\n" +
                    "Hour of Day: " + hour;

            displayValues.setText(displayText);
            displayValues.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Center the map on Kenya
        LatLng kenya = new LatLng(-1.2921, 36.8219); // Coordinates for Nairobi, Kenya
        googleMap.addMarker(new MarkerOptions().position(kenya).title("Nairobi, Kenya"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kenya, 6.0f)); // Zoom level 6
    }
}
