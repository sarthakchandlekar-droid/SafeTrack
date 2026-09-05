package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class HomeActivity extends AppCompatActivity {

    // API KEY = "paste the key here"

    private boolean isMapExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_home);

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
}
