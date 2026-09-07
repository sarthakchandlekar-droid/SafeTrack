package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.LinkedList;
import java.util.Queue;

public class SignalReceiverManager {

    private static final String TAG = "SignalReceiverManager";
    private static final long WINDOW_TIME_MS = 30_000; // 30 seconds time window
    private static final int CRITICAL_THRESHOLD = 5;    // 5 signals in 30s triggers SOS

    private static final Queue<Long> signalTimestamps = new LinkedList<>();

    public enum ThreatLevel {
        NORMAL_UPDATE,  // 🟢 Normal background updates (1 signal / 10 min)
        CRITICAL_SOS    // 🚨 Critical SOS Alert (5 signals within 30 seconds)
    }

    /**
     * Parses incoming signal, updates frequency tracker, filters noise,
     * and triggers Critical SOS window if threshold is met.
     */
    public static synchronized ThreatLevel processIncomingSignal(Context context, double lat, double lng, String locationName) {
        long currentTime = System.currentTimeMillis();
        signalTimestamps.add(currentTime);

        // Remove signals older than 30 seconds window
        while (!signalTimestamps.isEmpty()) {
            Long oldest = signalTimestamps.peek();
            if (oldest != null && (currentTime - oldest) > WINDOW_TIME_MS) {
                signalTimestamps.poll();
            } else {
                break;
            }
        }

        int signalCount = signalTimestamps.size();
        Log.d(TAG, "Signal received. Current frequency count in 30s window: " + signalCount);

        if (signalCount >= CRITICAL_THRESHOLD) {
            // Threshold met: Clear timestamps to prevent immediate re-triggering
            signalTimestamps.clear();

            // Trigger Critical SOS Alert window immediately
            Intent sosIntent = new Intent(context, SosAlertActivity.class);
            sosIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            sosIntent.putExtra("LATITUDE", lat);
            sosIntent.putExtra("LONGITUDE", lng);
            sosIntent.putExtra("LOCATION_NAME", locationName);
            context.startActivity(sosIntent);

            return ThreatLevel.CRITICAL_SOS;
        }

        return ThreatLevel.NORMAL_UPDATE;
    }
}
