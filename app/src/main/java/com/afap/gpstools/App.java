package com.afap.gpstools;

import android.app.Application;


public class App extends Application {
    private final String TAG = "App";

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized App getInstance() {
        return mInstance;
    }


}
