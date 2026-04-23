package com.example.manageit.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;


public class NetworkMonitor {

    public interface Listener {
        void onConnectivityChanged(boolean isConnected);
    }

    private final ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    public NetworkMonitor(Context context) {
        this.connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void start(Listener listener) {
        if (networkCallback != null || connectivityManager == null) {
            return;
        }

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                listener.onConnectivityChanged(true);
            }

            @Override
            public void onLost(Network network) {
                listener.onConnectivityChanged(false);
            }
        };

        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    public void stop() {
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
            networkCallback = null;
        }
    }
}
