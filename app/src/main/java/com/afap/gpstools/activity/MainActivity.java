package com.afap.gpstools.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
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
import android.widget.TextView;

import com.afap.gpstools.R;
import com.afap.gpstools.utils.LogUtil;
import com.afap.gpstools.widget.CompassView;
import com.afap.gpstools.widget.LevelView;
import com.afap.gpstools.widget.PointerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    private final static String TAG = "MainActivity";

    private SensorManager sensorManager;
    private Sensor aSensor; // 加速度传感器
    private Sensor mfSensor; // 地磁传感器

    private CompassView compassView;
    private LevelView levelView;
    private PointerView pointerView;

    private TextView tv_info_detail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tv_info_detail = (TextView) findViewById(R.id.info_detail);
        compassView = (CompassView) findViewById(R.id.compassView);
        levelView = (LevelView) findViewById(R.id.levelView);
        pointerView = (PointerView) findViewById(R.id.pointerView);
        compassView.postDelayed(new Runnable() {
            @Override
            public void run() {
                levelView.setRadius(compassView.getRadius());
            }
        }, 100);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mfSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
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
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            LogUtil.i(TAG, "xxxxxxxxxxxxxxxxxxxx:"  );
        }
        LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);//1.通过GPS定位，较精确，也比较耗电
        LocationProvider netProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);//2.通过网络定位，对定位精度度不高或省点情况可考虑使用
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0f, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LogUtil.i(TAG, "onLocationChanged:" + location.toString()  );
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LogUtil.i(TAG, "onStatusChanged:" + provider.toString()  );
            }

            @Override
            public void onProviderEnabled(String provider) {
                LogUtil.i(TAG, "onProviderEnabled:" + provider.toString()  );
            }

            @Override
            public void onProviderDisabled(String provider) {
                LogUtil.i(TAG, "onProviderDisabled:" + provider.toString()  );
            }
        });


    }

    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL, 16000);
        sensorManager.registerListener(this, mfSensor, SensorManager.SENSOR_DELAY_NORMAL, 16000);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
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
            return;
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
        }
        float[] values = new float[3];
        float[] rotation = new float[9];

        SensorManager.getRotationMatrix(rotation, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(rotation, values);


        values[0] = (float) Math.toDegrees(values[0]);


        StringBuffer sb = new StringBuffer();
        sb.append(getString(R.string.azimuth_f, values[0]));
        sb.append("\n" + getString(R.string.pitch_f, Math.toDegrees(values[1])));
        sb.append("\n" + getString(R.string.roll_f, Math.toDegrees(values[2])));

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

        tv_info_detail.setText(sb.toString());
        pointerView.updateAzimuth(values[0]);
        levelView.updateXY(values[1], values[2]);

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
