package com.example.guardianai.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.guardianai.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;
import okhttp3.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Recording extends AppCompatActivity {

    private static final String TAG = "Recording";
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private static final String API_URL = "https://guardianai-m2kc.onrender.com/analyze_audio";
    private static final long RECORDING_INTERVAL_MS = 5000; // 5 seconds
    private static final int API_TIMEOUT_MS = 60000; // 60 seconds

    private AudioRecord audioRecord;
    private File audioFile;
    private File wavFile;
    private Handler handler;
    private Runnable saveAndSendAudioRunnable;
    private boolean isRecording = false;
    private ImageView micIcon;
    private TextView recordingControlText;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording); // Ensure this matches your layout file

        micIcon = findViewById(R.id.mic_icon);
        recordingControlText = findViewById(R.id.recording_control_text);

        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);

        handler = new Handler(Looper.getMainLooper());

        recordingControlText.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        checkPermissions();
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            recordingControlText.setText("Stop Recording");
            Log.d(TAG, "Starting recording");

            micIcon.startAnimation(pulseAnimation); // Start pulse animation

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Record audio permission not granted");
                return;
            }

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

            // Create a unique filename for each recording
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            audioFile = new File(getExternalFilesDir(null), "audio_" + timestamp + ".pcm");

            try {
                if (audioFile.createNewFile()) {
                    Log.d(TAG, "Audio file created: " + audioFile.getAbsolutePath());
                } else {
                    Log.d(TAG, "Audio file already exists: " + audioFile.getAbsolutePath());
                }
            } catch (IOException e) {
                Log.e(TAG, "Error creating audio file", e);
            }

            audioRecord.startRecording();

            // Schedule saving and sending audio after 5 seconds
            saveAndSendAudioRunnable = this::saveAndSendAudioFileToApi;
            handler.postDelayed(saveAndSendAudioRunnable, RECORDING_INTERVAL_MS);
            Log.d(TAG, "Recording started");
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recordingControlText.setText("Start Recording");
            Log.d(TAG, "Stopping recording");

            micIcon.clearAnimation(); // Stop pulse animation

            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                handler.removeCallbacks(saveAndSendAudioRunnable);
                Log.d(TAG, "Recording stopped");
            }
        }
    }

    private void saveAndSendAudioFileToApi() {
        if (audioFile != null && audioFile.exists()) {
            // Convert PCM to WAV format
            wavFile = new File(getExternalFilesDir(null), "audio_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".wav");
            try {
                convertPcmToWav(audioFile, wavFile);

                if (wavFile.getName().endsWith(".wav")) {
                    Log.d(TAG, "Audio file is a valid WAV file, preparing to send to API: " + wavFile.getAbsolutePath());

                    new Thread(() -> {
                        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                        builder.addFormDataPart("file", wavFile.getName(), RequestBody.create(MediaType.parse("audio/wav"), wavFile));

                        RequestBody requestBody = builder.build();
                        Request request = new Request.Builder()
                                .url(API_URL)
                                .post(requestBody)
                                .addHeader("Content-Type", "audio/wav")
                                .build();

                        OkHttpClient client = new OkHttpClient.Builder()
                                .callTimeout(API_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e(TAG, "Error sending audio file", e);
                                runOnUiThread(() -> {
                                    Toast.makeText(Recording.this, "Failed to send audio file", Toast.LENGTH_SHORT).show();
                                });
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                Log.d(TAG, "API Response Code: " + response.code());
                                Log.d(TAG, "API Response Message: " + response.message());

                                String errorBody = response.body() != null ? response.body().string() : "No error body";
                                Log.e(TAG, "Error Body: " + errorBody);

                                if (response.isSuccessful()) {
                                    Log.d(TAG, "Audio file sent successfully");
                                    if (wavFile.delete()) {
                                        Log.d(TAG, "Audio file deleted: " + wavFile.getAbsolutePath());
                                    } else {
                                        Log.e(TAG, "Failed to delete audio file: " + wavFile.getAbsolutePath());
                                    }
                                } else {
                                    Log.e(TAG, "Failed to send audio file, response code: " + response.code() + ", response message: " + response.message());
                                    runOnUiThread(() -> {
                                        Toast.makeText(Recording.this, "Failed to send audio file", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        });
                    }).start();
                } else {
                    Log.e(TAG, "File is not a valid WAV file: " + wavFile.getAbsolutePath());
                    runOnUiThread(() -> {
                        Toast.makeText(Recording.this, "Invalid file format", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                Log.e(TAG, "Error converting PCM to WAV", e);
            }
        } else {
            Log.e(TAG, "Audio file does not exist");
        }
    }

    private void convertPcmToWav(File pcmFile, File wavFile) throws IOException {
        try (FileOutputStream out = new FileOutputStream(wavFile);
             InputStream in = new FileInputStream(pcmFile)) {

            byte[] header = new byte[44];
            int sampleRate = SAMPLE_RATE;
            int bitsPerSample = 16;
            int channels = 1;
            int totalDataLen = (int) pcmFile.length() + 36;
            int byteRate = sampleRate * channels * bitsPerSample / 8;

            // RIFF chunk descriptor
            header[0] = 'R';
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';
            writeInt(out, totalDataLen);
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';

            // Format
            header[12] = 'f';
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';
            header[16] = 16;
            header[20] = 1;
            header[22] = (byte) channels;
            writeInt(out, sampleRate);
            writeInt(out, byteRate);
            header[32] = (byte) (channels * bitsPerSample / 8);
            header[34] = (byte) bitsPerSample;

            // Data
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';
            writeInt(out, (int) pcmFile.length());

            out.write(header, 0, 44);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            Log.d(TAG, "PCM to WAV conversion completed");
        }
    }

    private void writeInt(FileOutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions granted");
            } else {
                Toast.makeText(this, "Permissions are required to record audio", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



//package com.example.guardianai.activities;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.example.guardianai.R;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okhttp3.MultipartBody;
//import okhttp3.MediaType;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class Recording extends AppCompatActivity {
//
//    private static final String TAG = "Recording";
//    private static final int REQUEST_CODE_PERMISSIONS = 1001;
//    private static final int SAMPLE_RATE = 16000;
//    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
//    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
//    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
//    private static final String API_URL = "https://guardianai-m2kc.onrender.com/analyze_audio";
//    private static final long RECORDING_INTERVAL_MS = 5000; // 5 seconds
//    private static final int API_TIMEOUT_MS = 60000; // 60 seconds
//
//    private AudioRecord audioRecord;
//    private File audioFile;
//    private File wavFile;
//    private Handler handler;
//    private Runnable saveAndSendAudioRunnable;
//    private boolean isRecording = false;
//    private ImageView micIcon;
//    private TextView recordingControlText;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recording); // Ensure this matches your layout file
//
//        micIcon = findViewById(R.id.mic_icon);
//        recordingControlText = findViewById(R.id.recording_control_text);
//
//        handler = new Handler();
//
//        recordingControlText.setOnClickListener(v -> {
//            if (isRecording) {
//                stopRecording();
//            } else {
//                startRecording();
//            }
//        });
//
//        checkPermissions();
//    }
//
//    private void checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    REQUEST_CODE_PERMISSIONS);
//        }
//    }
//
//    private void startRecording() {
//        if (!isRecording) {
//            isRecording = true;
//            recordingControlText.setText("Stop Recording");
//            Log.d(TAG, "Starting recording");
//
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                Log.e(TAG, "Record audio permission not granted");
//                return;
//            }
//
//            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
//
//            // Create a unique filename for each recording
//            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//            audioFile = new File(getExternalFilesDir(null), "audio_" + timestamp + ".pcm");
//
//            try {
//                if (audioFile.createNewFile()) {
//                    Log.d(TAG, "Audio file created: " + audioFile.getAbsolutePath());
//                } else {
//                    Log.d(TAG, "Audio file already exists: " + audioFile.getAbsolutePath());
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Error creating audio file", e);
//            }
//
//            audioRecord.startRecording();
//
//            // Schedule saving and sending audio after 5 seconds
//            saveAndSendAudioRunnable = this::saveAndSendAudioFileToApi;
//            handler.postDelayed(saveAndSendAudioRunnable, RECORDING_INTERVAL_MS);
//            Log.d(TAG, "Recording started");
//        }
//    }
//
//    private void stopRecording() {
//        if (isRecording) {
//            isRecording = false;
//            recordingControlText.setText("Start Recording");
//            Log.d(TAG, "Stopping recording");
//
//            if (audioRecord != null) {
//                audioRecord.stop();
//                audioRecord.release();
//                audioRecord = null;
//                micIcon.clearAnimation();
//                handler.removeCallbacks(saveAndSendAudioRunnable);
//                Log.d(TAG, "Recording stopped");
//            }
//        }
//    }
//
//    private void saveAndSendAudioFileToApi() {
//        if (audioFile != null && audioFile.exists()) {
//            // Convert PCM to WAV format
//            wavFile = new File(getExternalFilesDir(null), "audio_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".wav");
//            try {
//                convertPcmToWav(audioFile, wavFile);
//
//                if (wavFile.getName().endsWith(".wav")) {
//                    Log.d(TAG, "Audio file is a valid WAV file, preparing to send to API: " + wavFile.getAbsolutePath());
//
//                    new Thread(() -> {
//                        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//                        builder.addFormDataPart("file", wavFile.getName(), RequestBody.create(MediaType.parse("audio/wav"), wavFile));
//
//                        RequestBody requestBody = builder.build();
//                        Request request = new Request.Builder()
//                                .url(API_URL)
//                                .post(requestBody)
//                                .addHeader("Content-Type", "audio/wav")
//                                .build();
//
//                        OkHttpClient client = new OkHttpClient.Builder()
//                                .callTimeout(API_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS)
//                                .build();
//
//                        client.newCall(request).enqueue(new Callback() {
//                            @Override
//                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                                Log.e(TAG, "Error sending audio file", e);
//                                runOnUiThread(() -> {
//                                    Toast.makeText(Recording.this, "Failed to send audio file", Toast.LENGTH_SHORT).show();
//                                });
//                            }
//
//                            @Override
//                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                                Log.d(TAG, "API Response Code: " + response.code());
//                                Log.d(TAG, "API Response Message: " + response.message());
//
//                                String errorBody = response.body() != null ? response.body().string() : "No error body";
//                                Log.e(TAG, "Error Body: " + errorBody);
//
//                                if (response.isSuccessful()) {
//                                    Log.d(TAG, "Audio file sent successfully");
//                                    if (wavFile.delete()) {
//                                        Log.d(TAG, "Audio file deleted: " + wavFile.getAbsolutePath());
//                                    } else {
//                                        Log.e(TAG, "Failed to delete audio file: " + wavFile.getAbsolutePath());
//                                    }
//                                } else {
//                                    Log.e(TAG, "Failed to send audio file, response code: " + response.code() + ", response message: " + response.message());
//                                    runOnUiThread(() -> {
//                                        Toast.makeText(Recording.this, "Failed to send audio file", Toast.LENGTH_SHORT).show();
//                                    });
//                                }
//                            }
//                        });
//                    }).start();
//                } else {
//                    Log.e(TAG, "File is not a valid WAV file: " + wavFile.getAbsolutePath());
//                    runOnUiThread(() -> {
//                        Toast.makeText(Recording.this, "Invalid file format", Toast.LENGTH_SHORT).show();
//                    });
//                }
//            } catch (IOException e) {
//                Log.e(TAG, "Error converting PCM to WAV", e);
//            }
//        } else {
//            Log.e(TAG, "Audio file does not exist");
//        }
//    }
//
//
//    private void convertPcmToWav(File pcmFile, File wavFile) throws IOException {
//        try (FileOutputStream out = new FileOutputStream(wavFile);
//             InputStream in = new FileInputStream(pcmFile)) {
//
//            byte[] header = new byte[44];
//            int sampleRate = SAMPLE_RATE;
//            int bitsPerSample = 16;
//            int channels = 1;
//            int totalDataLen = (int) pcmFile.length() + 36;
//            int byteRate = sampleRate * channels * bitsPerSample / 8;
//
//            // RIFF chunk descriptor
//            header[0] = 'R';
//            header[1] = 'I';
//            header[2] = 'F';
//            header[3] = 'F';
//            writeInt(out, totalDataLen);
//            header[8] = 'W';
//            header[9] = 'A';
//            header[10] = 'V';
//            header[11] = 'E';
//
//            // Format
//            header[12] = 'f';
//            header[13] = 'm';
//            header[14] = 't';
//            header[15] = ' ';
//            writeInt(out, 16); // Subchunk1Size
//            writeShort(out, (short) 1); // AudioFormat
//            writeShort(out, (short) channels); // NumChannels
//            writeInt(out, sampleRate); // SampleRate
//            writeInt(out, byteRate); // ByteRate
//            writeShort(out, (short) (channels * bitsPerSample / 8)); // BlockAlign
//            writeShort(out, (short) bitsPerSample); // BitsPerSample
//
//            // Data
//            header[36] = 'd';
//            header[37] = 'a';
//            header[38] = 't';
//            header[39] = 'a';
//            writeInt(out, (int) pcmFile.length());
//
//            out.write(header);
//            byte[] buffer = new byte[BUFFER_SIZE];
//            int bytesRead;
//            while ((bytesRead = in.read(buffer)) != -1) {
//                out.write(buffer, 0, bytesRead);
//            }
//        }
//    }
//
//    private void writeInt(FileOutputStream out, int value) throws IOException {
//        out.write(value & 0xff);
//        out.write((value >> 8) & 0xff);
//        out.write((value >> 16) & 0xff);
//        out.write((value >> 24) & 0xff);
//    }
//
//    private void writeShort(FileOutputStream out, short value) throws IOException {
//        out.write(value & 0xff);
//        out.write((value >> 8) & 0xff);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (isRecording) {
//            stopRecording();
//        }
//    }
//}


