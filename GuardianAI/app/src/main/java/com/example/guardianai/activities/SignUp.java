package com.example.guardianai.activities;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardianai.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "SignUp";
    private static final String REGISTER_URL = "https://guardian-backend-6lro.onrender.com/api/v1/user/register";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText fullNameEditText = findViewById(R.id.full_name);
        EditText emailEditText = findViewById(R.id.email);
        EditText phoneNumberEditText = findViewById(R.id.phone_number);
        EditText emergencyContactEditText = findViewById(R.id.emergency_contact);
        EditText passwordEditText = findViewById(R.id.password);
        Button signUpButton = findViewById(R.id.btn_sign_up);

        signUpButton.setOnClickListener(v -> {
            String fullName = fullNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();
            String emergencyContact = emergencyContactEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("name", fullName);
                jsonBody.put("email", email);
                jsonBody.put("phone_number", phoneNumber);
                jsonBody.put("emergency_contacts", emergencyContact);
                jsonBody.put("password", password);

                registerUser(jsonBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        TextView loginTextView = findViewById(R.id.already_have_account);
        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this, SignIn.class);
            startActivity(intent);
        });
    }

    private void registerUser(JSONObject jsonBody) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(REGISTER_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Registration failed: " + e.getMessage());
                    Toast.makeText(SignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SignUp.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp.this, SignIn.class);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> {
                        Log.e(TAG, "Registration failed: " + response.message());
                        Toast.makeText(SignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}