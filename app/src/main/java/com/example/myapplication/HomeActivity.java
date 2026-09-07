package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private boolean isMapExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_home);

        // Request runtime permissions (GPS/Location, Notifications, Audio) for first-time user
        checkAndRequestPermissions();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Get parent name passed from LoginActivity or SharedPreferences
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName == null || userName.isEmpty()) {
            userName = prefs.getString("PARENT_NAME", "Guardian");
        }

        TextView tvGreeting = findViewById(R.id.tv_greeting_name);
        if (tvGreeting != null) {
            tvGreeting.setText(getString(R.string.hello_name, userName));
        }

        // Get registered child details
        String childName = getIntent().getStringExtra("CHILD_NAME");
        if (childName == null || childName.isEmpty()) {
            childName = prefs.getString("CHILD_NAME", "");
        }

        String childRelation = getIntent().getStringExtra("CHILD_RELATION");
        if (childRelation == null || childRelation.isEmpty()) {
            childRelation = prefs.getString("CHILD_RELATION", "");
        }

        String childDevice = getIntent().getStringExtra("CHILD_DEVICE");
        if (childDevice == null || childDevice.isEmpty()) {
            childDevice = prefs.getString("CHILD_DEVICE", "");
        }

        TextView tvChildName = findViewById(R.id.tv_person_name);
        TextView tvChildDetails = findViewById(R.id.tv_person_details);
        TextView tvChildAvatarInitial = findViewById(R.id.tv_child_avatar_initial);

        if (!childName.isEmpty()) {
            tvChildName.setText(childName);
            if (tvChildAvatarInitial != null) {
                tvChildAvatarInitial.setText(String.valueOf(childName.charAt(0)).toUpperCase());
            }
        } else {
            tvChildName.setText("Child");
            if (tvChildAvatarInitial != null) {
                tvChildAvatarInitial.setText("C");
            }
        }

        StringBuilder details = new StringBuilder();
        if (!childRelation.isEmpty()) {
            details.append(childRelation);
        } else {
            details.append("Child");
        }

        if (!childDevice.isEmpty()) {
            details.append(" · SafeTrack Band #").append(childDevice);
        } else {
            details.append(" · SafeTrack Band");
        }
        tvChildDetails.setText(details.toString());

        // Full map toggle functionality
        TextView tvFullMap = findViewById(R.id.tv_full_map);
        MaterialCardView cardMap = findViewById(R.id.card_map);

        if (tvFullMap != null && cardMap != null) {
            tvFullMap.setOnClickListener(v -> {
                ViewGroup.LayoutParams params = cardMap.getLayoutParams();
                int density = (int) getResources().getDisplayMetrics().density;

                if (!isMapExpanded) {
                    // Expand map window
                    params.height = 450 * density;
                    tvFullMap.setText("Close map");
                    isMapExpanded = true;
                } else {
                    // Collapse map window back to normal size
                    params.height = 200 * density;
                    tvFullMap.setText(getString(R.string.full_map));
                    isMapExpanded = false;
                }
                cardMap.setLayoutParams(params);
            });
        }

        // Status badge / Map pin signal receiver simulation trigger
        MaterialCardView statusBadge = findViewById(R.id.status_badge);
        ImageView mapPin = findViewById(R.id.map_pin);

        View.OnClickListener signalTriggerListener = v -> {
            SignalReceiverManager.ThreatLevel level = SignalReceiverManager.processIncomingSignal(
                    HomeActivity.this,
                    19.0760, 72.8777,
                    "Near MG Road Metro Station, Bandra West"
            );

            if (level == SignalReceiverManager.ThreatLevel.NORMAL_UPDATE) {
                Toast.makeText(HomeActivity.this, "Signal Received (Background update / Panic tap detected)", Toast.LENGTH_SHORT).show();
            }
        };

        if (statusBadge != null) statusBadge.setOnClickListener(signalTriggerListener);
        if (mapPin != null) mapPin.setOnClickListener(signalTriggerListener);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(HomeActivity.this, AlertHistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                Toast.makeText(this, "All permissions granted! SafeTrack active.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
