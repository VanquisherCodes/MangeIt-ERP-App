package com.example.manageit;

import android.app.Application;

import com.example.manageit.di.AppContainer;
import com.example.manageit.managers.UiModeManager;

public class ManageItApplication extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        UiModeManager.applySavedMode(this);
        appContainer = new AppContainer(this);
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
