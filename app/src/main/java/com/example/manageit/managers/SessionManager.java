package com.example.manageit.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.manageit.models.Role;
import com.example.manageit.utils.Constants;

/**
 * Persistent login session and role storage.
 */
public class SessionManager {

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createSession(String userName, Role role) {
        preferences.edit()
                .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                .putString(Constants.KEY_USER_NAME, userName)
                .putString(Constants.KEY_USER_ROLE, role.name())
                .apply();
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return preferences.getString(Constants.KEY_USER_NAME, "");
    }

    public Role getRole() {
        return Role.from(preferences.getString(Constants.KEY_USER_ROLE, Role.USER.name()));
    }
}
