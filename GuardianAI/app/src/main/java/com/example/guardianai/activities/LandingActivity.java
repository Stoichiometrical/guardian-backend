package com.example.guardianai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardianai.R;
import com.example.guardianai.activities.LocationSharing;
import com.example.guardianai.activities.SignUp;

public class LandingActivity extends AppCompatActivity {

    private static final long DELAY_MILLIS = 2000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Check if user ID is available in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null);

        if (userId != null) {
            // If user ID is available, proceed to LocationSharing activity after 3 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LandingActivity.this, LocationSharing.class);
                    startActivity(intent);
                    finish(); // Optional: Call finish() to remove this activity from the back stack
                }
            }, DELAY_MILLIS);
            return;
        }

        // Continue with Email button click event
        Button continueWithEmailButton = findViewById(R.id.btn_continue_with_email);
        continueWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Skip for Now text click event
        TextView skipForNowText = findViewById(R.id.skip_for_now);
        skipForNowText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, LocationSharing.class);
                startActivity(intent);
            }
        });
    }
}


//package com.example.guardianai.activities;
//
//import static androidx.core.content.ContextCompat.startActivity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.guardianai.R;
//import com.example.guardianai.activities.LocationSharing;
//import com.example.guardianai.activities.SignUp;
//
//public class LandingActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_landing);
//
//        // Continue with Email button click event
//        Button continueWithEmailButton = findViewById(R.id.btn_continue_with_email);
//        continueWithEmailButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LandingActivity.this, SignUp.class);
//                startActivity(intent);
//            }
//        });
//
//        // Skip for Now text click event
//        TextView skipForNowText = findViewById(R.id.skip_for_now);
//        skipForNowText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LandingActivity.this, LocationSharing.class);
//                startActivity(intent);
//            }
//        });
//    }
//}
