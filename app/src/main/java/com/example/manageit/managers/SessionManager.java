package com.example.manageit.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.manageit.models.User;
import com.example.manageit.utils.Constants;

/**
 * Persistent login session storage for the authenticated user only.
 */
public class SessionManager {

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void createSession(User user) {
        preferences.edit()
                .putBoolean(Constants.KEY_IS_LOGGED_IN, true)
                .putString(Constants.KEY_USER_ID, user.getId())
                .putString(Constants.KEY_USER_FIRST_NAME, user.getFirstName())
                .putString(Constants.KEY_USER_LAST_NAME, user.getLastName())
                .putString(Constants.KEY_USER_DOB, user.getDateOfBirth())
                .putString(Constants.KEY_USER_EMAIL, user.getEmail())
                .apply();
    }

    public void clearSession() {
        preferences.edit()
                .remove(Constants.KEY_IS_LOGGED_IN)
                .remove(Constants.KEY_USER_ID)
                .remove(Constants.KEY_USER_FIRST_NAME)
                .remove(Constants.KEY_USER_LAST_NAME)
                .remove(Constants.KEY_USER_DOB)
                .remove(Constants.KEY_USER_EMAIL)
                .apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return preferences.getString(Constants.KEY_USER_ID, "");
    }

    public String getUserFirstName() {
        return preferences.getString(Constants.KEY_USER_FIRST_NAME, "");
    }

    public String getUserLastName() {
        return preferences.getString(Constants.KEY_USER_LAST_NAME, "");
    }

    public String getUserDateOfBirth() {
        return preferences.getString(Constants.KEY_USER_DOB, "");
    }

    public String getUserEmail() {
        return preferences.getString(Constants.KEY_USER_EMAIL, "");
    }

    public User getCurrentUser() {
        return new User(
                getUserId(),
                getUserFirstName(),
                getUserLastName(),
                getUserDateOfBirth(),
                getUserEmail(),
                ""
        );
    }
}
