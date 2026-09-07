package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class AlertHistoryActivity extends AppCompatActivity {

    private TextView chipAll, chipSos, chipDaily;
    private MaterialCardView cardSos1, cardDaily1, cardDaily2, cardSos2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_hestory);

        // Bind filter chips
        chipAll = findViewById(R.id.chip_all);
        chipSos = findViewById(R.id.chip_sos);
        chipDaily = findViewById(R.id.chip_daily);

        // Bind alert cards
        cardSos1 = findViewById(R.id.card_sos_1);
        cardDaily1 = findViewById(R.id.card_daily_1);
        cardDaily2 = findViewById(R.id.card_daily_2);
        cardSos2 = findViewById(R.id.card_sos_2);

        // Set chip click listeners
        if (chipAll != null) chipAll.setOnClickListener(v -> selectFilter("ALL"));
        if (chipSos != null) chipSos.setOnClickListener(v -> selectFilter("SOS"));
        if (chipDaily != null) chipDaily.setOnClickListener(v -> selectFilter("DAILY"));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_alerts);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(AlertHistoryActivity.this, HomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_alerts) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(AlertHistoryActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void selectFilter(String filter) {
        // Reset chip styles
        chipAll.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipAll.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        chipSos.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipSos.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        chipDaily.setBackgroundResource(R.drawable.bg_chip_unselected);
        chipDaily.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));

        if ("ALL".equals(filter)) {
            chipAll.setBackgroundResource(R.drawable.bg_chip_selected);
            chipAll.setTextColor(ContextCompat.getColor(this, R.color.white));

            if (cardSos1 != null) cardSos1.setVisibility(View.VISIBLE);
            if (cardDaily1 != null) cardDaily1.setVisibility(View.VISIBLE);
            if (cardDaily2 != null) cardDaily2.setVisibility(View.VISIBLE);
            if (cardSos2 != null) cardSos2.setVisibility(View.VISIBLE);

        } else if ("SOS".equals(filter)) {
            chipSos.setBackgroundResource(R.drawable.bg_chip_selected);
            chipSos.setTextColor(ContextCompat.getColor(this, R.color.white));

            if (cardSos1 != null) cardSos1.setVisibility(View.VISIBLE);
            if (cardDaily1 != null) cardDaily1.setVisibility(View.GONE);
            if (cardDaily2 != null) cardDaily2.setVisibility(View.GONE);
            if (cardSos2 != null) cardSos2.setVisibility(View.VISIBLE);

        } else if ("DAILY".equals(filter)) {
            chipDaily.setBackgroundResource(R.drawable.bg_chip_selected);
            chipDaily.setTextColor(ContextCompat.getColor(this, R.color.white));

            if (cardSos1 != null) cardSos1.setVisibility(View.GONE);
            if (cardDaily1 != null) cardDaily1.setVisibility(View.VISIBLE);
            if (cardDaily2 != null) cardDaily2.setVisibility(View.VISIBLE);
            if (cardSos2 != null) cardSos2.setVisibility(View.GONE);
        }
    }
}
