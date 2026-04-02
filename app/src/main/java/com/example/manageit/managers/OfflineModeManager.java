package com.example.manageit.managers;

import android.content.Context;

import com.example.manageit.network.NetworkMonitor;

/**
 * Tracks online/offline state and toggles offline fallback behavior.
 */
public class OfflineModeManager {

    private final NetworkMonitor networkMonitor;
    private boolean offlineMode;

    public OfflineModeManager(Context context) {
        this.networkMonitor = new NetworkMonitor(context);
    }

    public void start() {
        networkMonitor.start(isConnected -> offlineMode = !isConnected);
    }

    public void stop() {
        networkMonitor.stop();
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }
}
