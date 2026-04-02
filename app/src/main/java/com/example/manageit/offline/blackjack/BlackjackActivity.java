package com.example.manageit.offline.blackjack;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.R;

/**
 * Isolated offline fallback module screen.
 */
public class BlackjackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blackjack);
    }
}
