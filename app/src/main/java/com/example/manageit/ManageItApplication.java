package com.example.manageit;

import android.app.Application;

import com.example.manageit.di.AppContainer;

public class ManageItApplication extends Application {

    private AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer(this);
    }

    public AppContainer getAppContainer() {
        return appContainer;
    }
}
