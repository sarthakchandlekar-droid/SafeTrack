package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_profile);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Display parent user name
        String parentName = prefs.getString("PARENT_NAME", "Guardian");
        TextView tvUserName = findViewById(R.id.tv_user_name);
        if (tvUserName != null) {
            tvUserName.setText(parentName);
        }

        // Display device details
        String childDevice = prefs.getString("CHILD_DEVICE", "");
        TextView tvDeviceName = findViewById(R.id.tv_device_name);
        if (tvDeviceName != null && !childDevice.isEmpty()) {
            tvDeviceName.setText("SafeTrack Band #" + childDevice);
        }

        // Display emergency contact number
        String parentPhone = prefs.getString("PARENT_PHONE", "");
        TextView tvEmergencyContactNumber = findViewById(R.id.tv_emergency_contact_number);
        if (tvEmergencyContactNumber != null && !parentPhone.isEmpty()) {
            tvEmergencyContactNumber.setText(parentPhone);
        }

        // Log Out Button functionality
        TextView btnLogout = findViewById(R.id.btn_logout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_alerts) {
                startActivity(new Intent(ProfileActivity.this, AlertHistoryActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }
}
