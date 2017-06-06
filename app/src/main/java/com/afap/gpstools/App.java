package com.afap.gpstools;

import android.app.Application;

import com.afap.utils.ToastUtil;


public class App extends Application {
    private final String TAG = "App";

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        ToastUtil.init(this);
    }

    public static synchronized App getInstance() {
        return mInstance;
    }


}
