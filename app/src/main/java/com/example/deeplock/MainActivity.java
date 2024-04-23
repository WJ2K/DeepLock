package com.example.deeplock;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.content.Intent;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor, proximitySensor, accelerometer;
    private static final int SENSOR_PERMISSION_REQUEST_CODE = 1;


    private static final int REQUEST_CHANGE_WIFI_STATE = 1;

    private WifiManager wifiManager;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestSensorPermissions();

        requestChangeWifiStatePermission();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Initialize sensor manager and get sensor instances
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void requestSensorPermissions() {
        // Check if permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, SENSOR_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, show user consent dialog
            showUserConsentDialog();
        }
    }

    private void requestChangeWifiStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CHANGE_WIFI_STATE}, REQUEST_CHANGE_WIFI_STATE);
        }
    }

    private void toggleWifi() {
        if (wifiManager != null) {
            boolean isWifiEnabled = wifiManager.isWifiEnabled();
            wifiManager.setWifiEnabled(!isWifiEnabled);
        }
    }

    private void showUserConsentDialog() {
        // Show a dialog or alert to get user consent for using DeepLock
        // If the user consents, start using the sensors
        Intent intent = new Intent(this, SensorService.class);
        startService(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listeners
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}