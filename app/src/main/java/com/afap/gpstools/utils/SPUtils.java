package com.afap.gpstools.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.afap.gpstools.App;


public class SPUtils {
    private final String SP_NAME = "mosaic_mini";

    public static final String KEY_GUIDE = "guide";// 是否需要显示引导页面

    private SharedPreferences sp;

    public SPUtils() {
        sp = App.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public SPUtils(Context context) {
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSP() {
        return sp;
    }

    public boolean needGuide() {
        return sp.getBoolean(KEY_GUIDE, true);
    }

    public void setGuide() {
        sp.edit().putBoolean(KEY_GUIDE, false).commit();
    }


}