package com.example.guardianai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guardianai.R;
import com.example.guardianai.activities.LocationSharing;
import com.example.guardianai.activities.Recording;
import com.example.guardianai.activities.SafeRoute;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;

                if (item.getItemId() == R.id.navigation_live_location) {
                    intent = new Intent(MainActivity.this, LocationSharing.class);
                } else if (item.getItemId() == R.id.navigation_safe_route) {
                    intent = new Intent(MainActivity.this, SafeRoute.class);
                } else if (item.getItemId() == R.id.navigation_recording) {
                    intent = new Intent(MainActivity.this, Recording.class);
                } else if (item.getItemId() == R.id.navigation_profile) {
                    intent = new Intent(MainActivity.this, Profile.class);
                }

                if (intent != null) {
                    startActivity(intent);
                    // Apply transition animation
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }

                return true; // Return true to indicate the item selection was handled
            }
        });
    }
}
