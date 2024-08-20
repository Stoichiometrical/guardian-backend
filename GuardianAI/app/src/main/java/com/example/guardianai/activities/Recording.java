package com.example.guardianai.activities;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.guardianai.R;

public class Recording extends AppCompatActivity {

    private ImageView micIcon;
    private TextView recordingControlText;
    private boolean isRecording = false; // To track the recording state
    private Animation pulseAnimation;

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
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            recordingControlText.setText("Start Recording");
            // Stop animations
            micIcon.clearAnimation();
        }
    }
}
