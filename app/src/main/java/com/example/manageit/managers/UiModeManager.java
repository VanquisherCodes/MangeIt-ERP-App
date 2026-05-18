package com.example.manageit.managers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.manageit.utils.Constants;

/**
 * Persists and applies the app-wide light/dark theme choice.
 */
public final class UiModeManager {

    private UiModeManager() {
    }

    public static void applySavedMode(Context context) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode(context) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static boolean isDarkMode(Context context) {
        return preferences(context).getBoolean(Constants.KEY_UI_MODE_DARK, true);
    }

    public static boolean toggleMode(Context context) {
        boolean nextDarkMode = !isDarkMode(context);
        preferences(context)
                .edit()
                .putBoolean(Constants.KEY_UI_MODE_DARK, nextDarkMode)
                .apply();
        AppCompatDelegate.setDefaultNightMode(
                nextDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        return nextDarkMode;
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }
}
