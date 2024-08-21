//package com.example.guardianai.activities;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.guardianai.R;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class Profile extends AppCompatActivity {
//
//    private EditText usernameEditText, emailEditText, phoneEditText, emergencyContactEditText, passwordEditText;
//    private Button saveButton, cancelButton;
//
//    private OkHttpClient client = new OkHttpClient();
//    private static final String UPDATE_PROFILE_URL = "https://guardian-backend-6lro.onrender.com/api/v1/user/profile";
//    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
//
//    private String userId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_profile);
//
//        // Initialize views
//        usernameEditText = findViewById(R.id.username);
//        emailEditText = findViewById(R.id.email);
//        phoneEditText = findViewById(R.id.phone);
//        emergencyContactEditText = findViewById(R.id.emergency_contact);
//        passwordEditText = findViewById(R.id.password);
//        saveButton = findViewById(R.id.btn_save);
//        cancelButton = findViewById(R.id.btn_cancel);
//
//        // Retrieve user ID from SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        userId = sharedPreferences.getString("user_id", null);
//
//        if (userId == null) {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
//            finish(); // Close the activity if user ID is not found
//        }
//
//        // Apply system insets
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Set listeners
//        saveButton.setOnClickListener(view -> saveProfile());
//        cancelButton.setOnClickListener(view -> finish());
//    }
//
//    private void saveProfile() {
//        String username = usernameEditText.getText().toString();
//        String email = emailEditText.getText().toString();
//        String phone = phoneEditText.getText().toString();
//        String emergencyContact = emergencyContactEditText.getText().toString();
//
//        // Create JSON object with the user data
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("user_id", userId); // Use the retrieved user ID
//            jsonObject.put("name", username);
//            jsonObject.put("phone_number", phone);
//            jsonObject.put("emergency_contacts", emergencyContact);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Create request body
//        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
//
//        // Create PUT request
//        Request request = new Request.Builder()
//                .url(UPDATE_PROFILE_URL)
//                .put(body)
//                .build();
//
//        // Execute request
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(() ->
//                        Toast.makeText(Profile.this, "Profile update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                );
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    runOnUiThread(() ->
//                            Toast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
//                    );
//                } else {
//                    runOnUiThread(() ->
//                            Toast.makeText(Profile.this, "Failed to update profile", Toast.LENGTH_SHORT).show()
//                    );
//                }
//            }
//        });
//    }
//}

package com.example.guardianai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Profile extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, phoneEditText, emergencyContactEditText, passwordEditText;
    private Button saveButton, cancelButton;

    private OkHttpClient client = new OkHttpClient();
    private static final String UPDATE_PROFILE_URL = "https://guardian-backend-6lro.onrender.com/api/v1/user/profile";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private String userId;

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

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if user ID is not found
        }

        // Apply system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set listeners
        saveButton.setOnClickListener(view -> saveProfile());
        cancelButton.setOnClickListener(view -> logout());
    }

    private void saveProfile() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String emergencyContact = emergencyContactEditText.getText().toString();

        // Create JSON object with the user data
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId); // Use the retrieved user ID
            jsonObject.put("name", username);
            jsonObject.put("phone_number", phone);
            jsonObject.put("emergency_contacts", emergencyContact);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create request body
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        // Create PUT request
        Request request = new Request.Builder()
                .url(UPDATE_PROFILE_URL)
                .put(body)
                .build();

        // Execute request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Profile.this, "Profile update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(Profile.this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void logout() {
        // Set user ID to null in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", null);
        editor.apply();

        // Redirect to LocationSharing activity
        Intent intent = new Intent(Profile.this, LocationSharing.class);
        startActivity(intent);
        finish(); // Optional: Call finish() to remove this activity from the back stack
    }
}

