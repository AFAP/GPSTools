package com.afap.gpstools.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.afap.gpstools.R;
import com.afap.gpstools.utils.LogUtil;
import com.afap.utils.ToastUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.Observer;

/**
 * 启动缓冲页面
 */
public class SplashActivity extends AppCompatActivity {
    private final static String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity instance

        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        LogUtil.i(TAG, "--onNext--" + aBoolean);
                        if (aBoolean) {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                        } else {
                            ToastUtil.showShort(R.string.permission_access_fine_location);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtil.i(TAG, "--onError--" + e.toString());
                    }

                    @Override
                    public void onCompleted() {
                        LogUtil.i(TAG, "--onCompleted--");
                    }
                });


//        Observable
//                .timer(800, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<Object>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Object o) {
//                        SPUtils mSpu = new SPUtils();
//
//                        if (mSpu.needGuide()) {
//                            mSpu.setGuide();
//                            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                });

    }

}
