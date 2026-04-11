package com.example.manageit.utils;

import com.example.manageit.models.User;

import java.time.LocalTime;

/**
 * Formats greeting text from the device's local time and user profile.
 */
public final class GreetingUtils {

    private GreetingUtils() {
    }

    public static String getGreetingForCurrentTime() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) {
            return "Good Morning,";
        }
        if (hour < 17) {
            return "Good Afternoon,";
        }
        return "Good Evening,";
    }

    public static String getDisplayFirstName(String fullName) {
        if (fullName == null) {
            return "there.";
        }

        String trimmed = fullName.trim();
        if (trimmed.isEmpty()) {
            return "there.";
        }

        int firstSpace = trimmed.indexOf(' ');
        String firstName = firstSpace > 0 ? trimmed.substring(0, firstSpace) : trimmed;
        return firstName + ".";
    }

    public static String getDisplayFirstName(User user) {
        if (user == null) {
            return "there.";
        }
        return getDisplayFirstName(user.getFullName());
    }

    public static String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "MI";
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }

    public static String getInitials(User user) {
        if (user == null) {
            return "MI";
        }
        return getInitials(user.getFullName());
    }
}
