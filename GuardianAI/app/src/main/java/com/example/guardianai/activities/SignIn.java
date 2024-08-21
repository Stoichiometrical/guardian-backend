package com.example.guardianai.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardianai.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class SignIn extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (validateInputs(email, password)) {
                    loginUser(email, password);
                }
            }
        });

        TextView signupText = findViewById(R.id.signup_text);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loginUser(String email, String password) {
        // Create a logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Setting timeout values to 30 seconds and adding the logging interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor) // Add logging interceptor
                .build();

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://guardian-backend-6lro.onrender.com/api/v1/user/login")
                .post(body)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        String userId = jsonResponse.getString("user_id");

                        // Save user ID to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_id", userId);
                        editor.apply();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SignIn.this, LocationSharing.class);
                                intent.putExtra("USER_ID", userId);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignIn.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (IOException | JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignIn.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}


//package com.example.guardianai.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.guardianai.R;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okhttp3.logging.HttpLoggingInterceptor;
//
//public class SignIn extends AppCompatActivity {
//
//    private EditText emailEditText, passwordEditText;
//    private Button loginButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_in);
//
//        emailEditText = findViewById(R.id.email);
//        passwordEditText = findViewById(R.id.password);
//        loginButton = findViewById(R.id.login_button);
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String email = emailEditText.getText().toString().trim();
//                String password = passwordEditText.getText().toString().trim();
//                if (validateInputs(email, password)) {
//                    loginUser(email, password);
//                }
//            }
//        });
//
//        TextView signupText = findViewById(R.id.signup_text);
//        signupText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SignIn.this, SignUp.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private boolean validateInputs(String email, String password) {
//        if (email.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    private void loginUser(String email, String password) {
//        // Create a logging interceptor
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        // Setting timeout values to 30 seconds and adding the logging interceptor
//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
//                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
//                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
//                .addInterceptor(loggingInterceptor) // Add logging interceptor
//                .build();
//
//        JSONObject json = new JSONObject();
//        try {
//            json.put("email", email);
//            json.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
//        Request request = new Request.Builder()
//                .url("https://guardian-backend-6lro.onrender.com/api/v1/user/login")
//                .post(body)
//                .build();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Response response = client.newCall(request).execute();
//                    if (response.isSuccessful()) {
//                        String responseData = response.body().string();
//                        JSONObject jsonResponse = new JSONObject(responseData);
//                        String userId = jsonResponse.getString("user_id");
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(SignIn.this, LocationSharing.class);
//                                intent.putExtra("USER_ID", userId);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(SignIn.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                } catch (IOException | JSONException e) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(SignIn.this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            }
//        }).start();
//    }
//
//
//}
