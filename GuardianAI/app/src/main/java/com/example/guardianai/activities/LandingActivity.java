package com.example.guardianai.activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardianai.R;
import com.example.guardianai.activities.LocationSharing;
import com.example.guardianai.activities.SignUp;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

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
