package com.afap.gpstools;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.afap.gpstools.utils.LogUtil;
import com.afap.gpstools.widget.CompassView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    private final static String TAG = "MainActivity";

    private SensorManager sensorManager;
    private Sensor aSensor; // 加速度传感器
    private Sensor mfSensor; // 地磁传感器

    private int width_compass = 0;
    private CompassView compassView;
    private TextView tv_info_detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tv_info_detail = (TextView) findViewById(R.id.info_detail);
        compassView = (CompassView) findViewById(R.id.compassView);
        width_compass = compassView.getWidth();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mfSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            LogUtil.d(TAG, "GnssStatus");
//            locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
//                @Override
//                public void onStarted() {
//                    super.onStarted();
//                    LogUtil.d(TAG, "GnssStatus：onStarted");
//                }
//
//
//                @Override
//                public void onSatelliteStatusChanged(GnssStatus status) {
//                    super.onSatelliteStatusChanged(status);
//
////                    int num = status.getSatelliteCount();
////                    LogUtil.d(TAG, "数量：" + num);
////                    for (int i = 0; i < num; i++) {
////                        String info = "卫星信息：\n信噪：" + status.getCn0DbHz(i);
////                        info += "\n高度：" + status.getElevationDegrees(i);
////                        LogUtil.d(TAG, info);
////                    }
//
//
//                }
//
//
//            });
//        } else {
        locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                GpsStatus status = locationManager.getGpsStatus(null);


                StringBuilder sb2 = new StringBuilder("");
                if (status == null) {
                    sb2.append("搜索到卫星个数：" + 0);
                } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                    int maxSatellites = status.getMaxSatellites();
                    Iterator<GpsSatellite> it = status.getSatellites().iterator();
                    numSatelliteList.clear();
                    int count = 0;
                    while (it.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = it.next();
                        numSatelliteList.add(s);



                        count++;
                    }
                    sb2.append("搜索到卫星个数：" + numSatelliteList.size());
                    compassView.updateGpsSatellite(numSatelliteList);
                }

                LogUtil.d(TAG, sb2.toString());

            }
        });
//        }


    }

    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL, 400000);
        sensorManager.registerListener(this, mfSensor, SensorManager.SENSOR_DELAY_NORMAL, 400000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, aSensor);
        sensorManager.unregisterListener(this, mfSensor);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private float v0;

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];


    /**
     * ********╲*******Y️
     * *********╲*****⬆️
     * **********╲****|**********
     * ***********╲***|**********
     * ************╲**|**********
     * *************╲*|**********
     * **************╲|**********
     * ------------------------------> X （手机宽度 左--右）
     * ***************|╲*********
     * ***************|*╲********
     * ***************|**╲*******
     * ***************|***╲******
     * ***************|****╲*****
     * ***************|*****╲****
     * ***************|******╲***
     * ***************|*******Z**（手机平放时朝上方向）
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values;

        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        float[] values = new float[3];
        float[] R = new float[9];

        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);


        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        String info = "[Z]Azimuth:" + values[0];
        info += "\n[X]Pitch:" + values[1];
        info += "\n[Y]Pitch:" + values[2];

//        LogUtil.i(TAG, info);

//        if (values[0] >= -5 && values[0] < 5) {
//            tv_info_detail.setText("正北");
//        } else if (values[0] >= 5 && values[0] < 85) {
//            // Log.i(TAG, "东北");
//            tv_info_detail.setText("东北");
//        } else if (values[0] >= 85 && values[0] <= 95) {
//            // Log.i(TAG, "正东");
//            tv_info_detail.setText("正东");
//        } else if (values[0] >= 95 && values[0] < 175) {
//            // Log.i(TAG, "东南");
//            tv_info_detail.setText("东南");
//        } else if ((values[0] >= 175 && values[0] <= 180)
//                || (values[0]) >= -180 && values[0] < -175) {
//            // Log.i(TAG, "正南");
//            tv_info_detail.setText("正南");
//        } else if (values[0] >= -175 && values[0] < -95) {
//            // Log.i(TAG, "西南");
//            tv_info_detail.setText("西南");
//        } else if (values[0] >= -95 && values[0] < -85) {
//            // Log.i(TAG, "正西");
//            tv_info_detail.setText("正西");
//        } else if (values[0] >= -85 && values[0] < -5) {
//            // Log.i(TAG, "西北");
//            tv_info_detail.setText("西北");
//        }

        tv_info_detail.setText(info);

        RotateAnimation animate;
        animate = new RotateAnimation(v0, values[0], 540, 540);
        v0 = values[0];
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
//        compassView.startAnimation(animate);


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        LogUtil.i(TAG, "onAccuracyChanged:" + sensor.getName() + ",accuracy=" + accuracy);
    }


//    /**
//     * 卫星状态监听器
//     */
//    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
//
//    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {
//        public void onGpsStatusChanged(int event) { // GPS状态变化时的回调，如卫星数
//
//            GpsStatus status = locationManager.getGpsStatus(null); //取当前状态
//            String satelliteInfo = updateGpsStatus(event, status);
//            tv_satellites.setText(null);
//            tv_satellites.setText(satelliteInfo);
//        }
//    };
//
//    private String updateGpsStatus(int event, GpsStatus status) {
//        StringBuilder sb2 = new StringBuilder("");
//        if (status == null) {
//            sb2.append("搜索到卫星个数：" + 0);
//        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
//            int maxSatellites = status.getMaxSatellites();
//            Iterator<GpsSatellite> it = status.getSatellites().iterator();
//            numSatelliteList.clear();
//            int count = 0;
//            while (it.hasNext() && count <= maxSatellites) {
//                GpsSatellite s = it.next();
//                numSatelliteList.add(s);
//                count++;
//            }
//            sb2.append("搜索到卫星个数：" + numSatelliteList.size());
//        }
//
//        return sb2.toString();
//    }


}
