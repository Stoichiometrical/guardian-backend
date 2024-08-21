package com.example.guardianai.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.guardianai.R;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Handle system bars inset
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Find the Login text view
        TextView loginTextView = findViewById(R.id.already_have_account);

        // Set onClick listener to navigate to SignIn activity
        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
        });

        // TODO: Add logic for signing up with the entered data.
    }
}
