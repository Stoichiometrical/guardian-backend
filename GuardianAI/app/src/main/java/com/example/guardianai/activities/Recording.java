//package com.example.guardianai.activities;
//
//import android.os.Bundle;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.guardianai.R;
//
//public class Recording extends AppCompatActivity {
//
//    private ImageView micIcon;
//    private TextView recordingControlText;
//    private boolean isRecording = false; // To track the recording state
//    private Animation pulseAnimation;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_recording);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        micIcon = findViewById(R.id.mic_icon);
//        recordingControlText = findViewById(R.id.recording_control_text);
//
//        // Load animations
//        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
//
//        // Set click listener on Recording Control Text
//        recordingControlText.setOnClickListener(v -> {
//            if (isRecording) {
//                stopRecording();
//            } else {
//                startRecording();
//            }
//        });
//    }
//
//    private void startRecording() {
//        if (!isRecording) {
//            isRecording = true;
//            recordingControlText.setText("Stop Recording");
//            // Start animations
//            micIcon.startAnimation(pulseAnimation);
//        }
//    }
//
//    private void stopRecording() {
//        if (isRecording) {
//            isRecording = false;
//            recordingControlText.setText("Start Recording");
//            // Stop animations
//            micIcon.clearAnimation();
//        }
//    }
//}



package com.example.guardianai.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.guardianai.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Recording extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final String API_URL = "https://guardianai-m2kc.onrender.com/analyze_audio";
    private static final MediaType MEDIA_TYPE_WAV = MediaType.parse("audio/wav");

    private ImageView micIcon;
    private TextView recordingControlText;
    private boolean isRecording = false; // To track the recording state
    private Animation pulseAnimation;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private Handler handler = new Handler();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recording);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        micIcon = findViewById(R.id.mic_icon);
        recordingControlText = findViewById(R.id.recording_control_text);

        // Load animations
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        // Request necessary permissions for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
            }, REQUEST_CODE_PERMISSIONS);
        }

        // Set click listener on Recording Control Text
        recordingControlText.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            recordingControlText.setText("Stop Recording");
            // Start animations
            micIcon.startAnimation(pulseAnimation);

            // Set up media recorder and start recording
            audioFilePath = getExternalFilesDir(null) + "/audio_recording.wav";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(audioFilePath);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Start sending audio every 5 seconds
            handler.postDelayed(sendAudioRunnable, 5000);
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recordingControlText.setText("Start Recording");
            // Stop animations
            micIcon.clearAnimation();

            // Stop recording
            if (mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
            }

            // Stop sending audio
            handler.removeCallbacks(sendAudioRunnable);
        }
    }

    private final Runnable sendAudioRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                sendAudioToApi();
                handler.postDelayed(this, 5000);
            }
        }
    };

    private void sendAudioToApi() {
        File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) return;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio", "audio_recording.wav",
                        RequestBody.create(audioFile, MEDIA_TYPE_WAV))
                .build();

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build();

        executorService.execute(() -> client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Recording", "Failed to send audio: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(Recording.this, "Alert sent", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e("Recording", "Failed to send audio: " + response.message());
                }
            }
        }));
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (!allPermissionsGranted) {
                Toast.makeText(this, "Permissions required to access media files", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

