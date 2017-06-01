package com.afap.gpstools.utils;

import android.util.Log;

public class LogUtil {

    public static boolean isDebug = true;

    public static int v(String tag, String msg) {
        return isDebug ? Log.v(tag, msg) : -1;
    }

    public static int i(String tag, String msg) {
        return isDebug ? Log.i(tag, msg) : -1;
    }

    public static int d(String tag, String msg) {
        return isDebug ? Log.d(tag, msg) : -1;
    }

    public static int w(String tag, String msg) {
        return isDebug ? Log.w(tag, msg) : -1;
    }

    public static int e(String tag, String msg) {
        return isDebug ? Log.e(tag, msg) : -1;
    }

}
