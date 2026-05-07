package com.example.manageit.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manageit.BuildConfig;
import com.example.manageit.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

/**
 * Displays information about the application and the current version.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        bindToolbar();
        displayVersion();
    }

    private void bindToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar_about);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void displayVersion() {
        TextView versionView = findViewById(R.id.tv_version_name);
        String version = String.format(Locale.getDefault(), "Version %s (%d)", 
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        versionView.setText(version);
    }
}
