package com.example.manageit.repository;

import java.util.Arrays;
import java.util.List;

/**
 * Shared module access placeholder for tasks/events/announcements/requests/profile.
 */
public class ModuleRepository {

    public List<String> getDefaultModules() {
        return Arrays.asList(
                "Profile",
                "Tasks",
                "Events",
                "Announcements",
                "Requests"
        );
    }
}
