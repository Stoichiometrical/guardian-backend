package com.example.guardianai.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.guardianai.R;

public class Profile extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, phoneEditText, emergencyContactEditText, passwordEditText;
    private Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Initialize views
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        emergencyContactEditText = findViewById(R.id.emergency_contact);
        passwordEditText = findViewById(R.id.password);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);

        // Apply system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set listeners
        saveButton.setOnClickListener(view -> saveProfile());
        cancelButton.setOnClickListener(view -> finish());
    }

    private void saveProfile() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String emergencyContact = emergencyContactEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Logic to save profile details

        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
