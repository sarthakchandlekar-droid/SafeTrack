package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SosAlertActivity extends AppCompatActivity {

    private Ringtone alarmRingtone;
    private int originalVolume;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_sos_alert);

        // Turn screen on and unlock if locked during emergency
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }

        // Trigger Full Volume Emergency Sound
        triggerFullVolumeAlarm();

        // Bind Action Buttons
        Button btnAcceptAlert = findViewById(R.id.btn_accept_alert);
        Button btnCallAnanya = findViewById(R.id.btn_call_ananya);
        Button btnCallPolice = findViewById(R.id.btn_call_police);
        Button btnFalseAlarmDismiss = findViewById(R.id.btn_false_alarm_dismiss);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String parentPhone = prefs.getString("PARENT_PHONE", "9876543210");

        // 1. Accept & Acknowledge Alert (System registers guardian has seen/accepted)
        if (btnAcceptAlert != null) {
            btnAcceptAlert.setOnClickListener(v -> {
                stopAlarm();
                Toast.makeText(SosAlertActivity.this, "Alert Accepted & Acknowledged by Guardian", Toast.LENGTH_LONG).show();
            });
        }

        // 2. Direct Call Child
        if (btnCallAnanya != null) {
            btnCallAnanya.setOnClickListener(v -> {
                stopAlarm();
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + parentPhone));
                startActivity(dialIntent);
            });
        }

        // 3. Suggestion Option for Police (Confirmation choice for Guardian)
        if (btnCallPolice != null) {
            btnCallPolice.setOnClickListener(v -> new AlertDialog.Builder(SosAlertActivity.this)
                    .setTitle("Emergency Advice Option")
                    .setMessage("As a guardian, would you like to dial local emergency services (112)?")
                    .setPositiveButton("Call 112", (dialog, which) -> {
                        stopAlarm();
                        Intent policeIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                        startActivity(policeIntent);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show());
        }

        // 4. False Alarm Dismiss Button (Centered at Bottom)
        if (btnFalseAlarmDismiss != null) {
            btnFalseAlarmDismiss.setOnClickListener(v -> {
                stopAlarm();
                Toast.makeText(SosAlertActivity.this, "False Alarm Dismissed", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void triggerFullVolumeAlarm() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                // Save current volume to restore later
                originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                
                // Set ALARM stream to 100% full volume
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);

                Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }

                alarmRingtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
                if (alarmRingtone != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alarmRingtone.setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build());
                    }
                    alarmRingtone.play();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAlarm() {
        try {
            if (alarmRingtone != null && alarmRingtone.isPlaying()) {
                alarmRingtone.stop();
            }
            if (audioManager != null) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, originalVolume, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }
}
