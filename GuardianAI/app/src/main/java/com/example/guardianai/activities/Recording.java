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


//package com.example.guardianai.activities;
//
//
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.media.MediaRecorder;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.guardianai.R;
//
//import java.io.File;
//import java.io.IOException;
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class Recording extends AppCompatActivity {
//
//    private static final String TAG = "RecordingActivity";
//    private static final int REQUEST_CODE_PERMISSIONS = 123;
//    private static final String API_URL = "https://yourapiendpoint.com/analyze_audio";
//
//    private ImageView micIcon;
//    private TextView recordingControlText;
//    private boolean isRecording = false;
//    private Animation pulseAnimation;
//    private MediaRecorder mediaRecorder;
//    private File audioFile;
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
//                if (checkPermissions()) {
//                    startRecording();
//                } else {
//                    requestPermissions();
//                }
//            }
//        });
//    }
//
//    private boolean checkPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            boolean recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
//            boolean readMediaAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
//            Log.d(TAG, "Check Permissions: Record Audio Permission: " + recordAudioPermission + ", Read Media Audio Permission: " + readMediaAudioPermission);
//            return recordAudioPermission && readMediaAudioPermission;
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            boolean recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
//            Log.d(TAG, "Check Permissions: Record Audio Permission: " + recordAudioPermission);
//            return recordAudioPermission;
//        }
//        return true;
//    }
//
//    private void requestPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
//                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)) {
//                ActivityCompat.requestPermissions(this, new String[]{
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.READ_MEDIA_AUDIO
//                }, REQUEST_CODE_PERMISSIONS);
//            } else {
//                showPermissionsSettingsMessage();
//            }
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSIONS);
//        }
//    }
//
//    private void showPermissionsSettingsMessage() {
//        Toast.makeText(this, "Permissions have been denied permanently. Please go to settings to enable them.", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        startActivity(intent);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            boolean allPermissionsGranted = true;
//            for (int result : grantResults) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    allPermissionsGranted = false;
//                    break;
//                }
//            }
//
//            if (allPermissionsGranted) {
//                Log.d(TAG, "Permissions granted, setting up recording");
//                startRecording();
//            } else {
//                Log.d(TAG, "Permissions not granted, requesting again");
//                showPermissionsSettingsMessage();
//            }
//        }
//    }
//
//    private void startRecording() {
//        if (!isRecording) {
//            isRecording = true;
//            recordingControlText.setText("Stop Recording");
//
//            // Initialize MediaRecorder
//            mediaRecorder = new MediaRecorder();
//            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//            // Create a file to save the recording
//            try {
//                audioFile = File.createTempFile("audio", ".3gp", getExternalFilesDir(null));
//                mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
//                mediaRecorder.prepare();
//                mediaRecorder.start();
//                Log.d(TAG, "Recording started, file: " + audioFile.getAbsolutePath());
//
//                // Start animations
//                micIcon.startAnimation(pulseAnimation);
//
//                // Send audio file to API after recording
//                micIcon.postDelayed(this::sendAudioFileToApi, 5000); // Example: wait for 5 seconds
//            } catch (IOException e) {
//                Log.e(TAG, "Failed to start recording: " + e.getMessage());
//                Toast.makeText(this, "Failed to start recording.", Toast.LENGTH_SHORT).show();
//                isRecording = false;
//            }
//        }
//    }
//
//    private void stopRecording() {
//        if (isRecording) {
//            isRecording = false;
//            recordingControlText.setText("Start Recording");
//
//            // Stop MediaRecorder
//            mediaRecorder.stop();
//            mediaRecorder.release();
//            mediaRecorder = null;
//            Log.d(TAG, "Recording stopped");
//
//            // Stop animations
//            micIcon.clearAnimation();
//        }
//    }
//
//    private void sendAudioFileToApi() {
//        // Check if the file is valid
//        if (audioFile != null && audioFile.exists()) {
//            // Implement the code to send the audio file to the API endpoint here
//            new Thread(() -> {
//                try {
//                    // Use an HTTP library to send the audio file
//                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//                    builder.addFormDataPart("file", audioFile.getName(), RequestBody.create(audioFile, MediaType.parse("audio/3gp")));
//                    RequestBody requestBody = builder.build();
//
//                    Request request = new Request.Builder()
//                            .url(API_URL)
//                            .post(requestBody)
//                            .build();
//
//                    OkHttpClient client = new OkHttpClient();
//                    Response response = client.newCall(request).execute();
//                    if (response.isSuccessful()) {
//                        String responseData = response.body().string();
//                        Log.d(TAG, "API Response: " + responseData);
//                        // Parse and handle the response
//                        runOnUiThread(() -> Toast.makeText(this, "Audio analyzed successfully", Toast.LENGTH_SHORT).show());
//                    } else {
//                        Log.e(TAG, "API Request failed: " + response.code());
//                        runOnUiThread(() -> Toast.makeText(this, "API Request failed", Toast.LENGTH_SHORT).show());
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "Error sending audio file to API: " + e.getMessage());
//                }
//            }).start();
//        } else {
//            Log.e(TAG, "Audio file is null or does not exist");
//        }
//    }
//}


package com.example.guardianai.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.guardianai.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Recording extends AppCompatActivity {

    private static final String TAG = "RecordingActivity";
    private static final int REQUEST_CODE_PERMISSIONS = 123;
    private static final String API_URL = "https://yourapiendpoint.com/analyze_audio";
    private static final int RECORDING_INTERVAL_MS = 5000; // 5 seconds

    private static final int SAMPLE_RATE = 44100; // Sample rate for WAV
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    private ImageView micIcon;
    private TextView recordingControlText;
    private boolean isRecording = false;
    private Animation pulseAnimation;
    private AudioRecord audioRecord;
    private File audioFile;

    private Handler handler;
    private Runnable sendAudioRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recording);

        // Initialize Handler
        handler = new Handler(Looper.getMainLooper());

        // Initialize the send audio runnable
        sendAudioRunnable = new Runnable() {
            @Override
            public void run() {
                sendAudioFileToApi();
                if (isRecording) {
                    // Schedule the next send
                    handler.postDelayed(this, RECORDING_INTERVAL_MS);
                }
            }
        };

        // Set up UI components
        micIcon = findViewById(R.id.mic_icon);
        recordingControlText = findViewById(R.id.recording_control_text);

        // Load animations
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        // Set click listener on Recording Control Text
        recordingControlText.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                if (checkPermissions()) {
                    startRecording();
                } else {
                    requestPermissions();
                }
            }
        });
    }

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            boolean recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            boolean readMediaAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "Check Permissions: Record Audio Permission: " + recordAudioPermission + ", Read Media Audio Permission: " + readMediaAudioPermission);
            return recordAudioPermission && readMediaAudioPermission;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "Check Permissions: Record Audio Permission: " + recordAudioPermission);
            return recordAudioPermission;
        }
        return true;
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_MEDIA_AUDIO
                }, REQUEST_CODE_PERMISSIONS);
            } else {
                showPermissionsSettingsMessage();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void showPermissionsSettingsMessage() {
        Toast.makeText(this, "Permissions have been denied permanently. Please go to settings to enable them.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

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

            if (allPermissionsGranted) {
                Log.d(TAG, "Permissions granted, setting up recording");
                startRecording();
            } else {
                Log.d(TAG, "Permissions not granted, requesting again");
                showPermissionsSettingsMessage();
            }
        }
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            recordingControlText.setText("Stop Recording");

            // Initialize AudioRecord
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
            audioFile = new File(getExternalFilesDir(null), "audio.wav");

            // Start recording
            audioRecord.startRecording();
            Log.d(TAG, "Recording started");

            // Start animations
            micIcon.startAnimation(pulseAnimation);

            // Start sending audio files every 5 seconds
            handler.postDelayed(sendAudioRunnable, RECORDING_INTERVAL_MS);
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recordingControlText.setText("Start Recording");

            // Stop recording
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            Log.d(TAG, "Recording stopped");

            // Stop animations
            micIcon.clearAnimation();
        }
    }

    private void sendAudioFileToApi() {
        if (audioFile != null && audioFile.exists()) {
            new Thread(() -> {
                try {
                    // Convert raw PCM to WAV format
                    convertPcmToWav(audioFile);

                    // Use an HTTP library to send the WAV file
                    MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                    builder.addFormDataPart("file", audioFile.getName(), RequestBody.create(audioFile, MediaType.parse("audio/wav")));
                    RequestBody requestBody = builder.build();

                    Request request = new Request.Builder()
                            .url(API_URL)
                            .post(requestBody)
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Log.d(TAG, "API Response: " + responseData);
                        runOnUiThread(() -> Toast.makeText(this, "Audio analyzed successfully", Toast.LENGTH_SHORT).show());
                    } else {
                        Log.e(TAG, "API Request failed: " + response.code());
                        runOnUiThread(() -> Toast.makeText(this, "API Request failed", Toast.LENGTH_SHORT).show());
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error sending audio file to API: " + e.getMessage());
                }
            }).start();
        } else {
            Log.e(TAG, "Audio file is null or does not exist");
        }
    }

    private void convertPcmToWav(File pcmFile) throws IOException {
        // Placeholder for PCM to WAV conversion logic
        File wavFile = new File(getExternalFilesDir(null), "converted_audio.wav");
        try (FileOutputStream fos = new FileOutputStream(wavFile);
             OutputStream os = new FileOutputStream(wavFile)) {
            // Write WAV header
            writeWavHeader(os, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
            // Write PCM data
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            try (FileInputStream fis = new FileInputStream(pcmFile)) {
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
        audioFile = wavFile;
    }

    private void writeWavHeader(OutputStream os, int sampleRate, int channelConfig, int audioFormat, int bufferSize) throws IOException {
        int byteRate = sampleRate * channelConfig * (audioFormat / 8);
        int totalDataLen = bufferSize + 36;
        int format = 1; // PCM

        os.write("RIFF".getBytes()); // Chunk ID
        writeInt(os, totalDataLen); // Chunk Size
        os.write("WAVE".getBytes()); // Format
        os.write("fmt ".getBytes()); // Subchunk 1 ID
        writeInt(os, 16); // Subchunk 1 Size
        writeShort(os, (short) format); // Audio Format
        writeShort(os, (short) (channelConfig == AudioFormat.CHANNEL_IN_MONO ? 1 : 2)); // Number of Channels
        writeInt(os, sampleRate); // Sample Rate
        writeInt(os, byteRate); // Byte Rate
        writeShort(os, (short) (channelConfig * (audioFormat / 8))); // Block Align
        writeShort(os, (short) audioFormat); // Bits Per Sample
        os.write("data".getBytes()); // Subchunk 2 ID
        writeInt(os, bufferSize); // Subchunk 2 Size
    }

    private void writeInt(OutputStream os, int value) throws IOException {
        os.write(value & 0xff);
        os.write((value >> 8) & 0xff);
        os.write((value >> 16) & 0xff);
        os.write((value >> 24) & 0xff);
    }

    private void writeShort(OutputStream os, short value) throws IOException {
        os.write(value & 0xff);
        os.write((value >> 8) & 0xff);
    }
}
