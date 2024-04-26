package com.example.deeplock;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.wifi.WifiManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.widget.Toast;

import android.widget.FrameLayout;
import android.view.View;
import android.graphics.Color;
import android.provider.Settings;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONS_REQUEST_CODE = 200;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT
    };

    private SensorListener sensorListener;

    private int originalBrightnessLevel;
    private View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if all required permissions are granted
        if (shouldRequestPermissions()) {
            Log.d(TAG, "Requesting permissions");
            requestPermissions();
        } else {
            Log.d(TAG, "Permissions already granted");
            initializeApp();
        }
    }

    private void initializeApp() {
        sensorListener = new SensorListener(this, this);
        originalBrightnessLevel = getOriginalBrightnessLevel();
    }

    private boolean shouldRequestPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    private void requestPermissions() {
        Log.d(TAG, "Requesting permissions...");
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            if (allPermissionsGranted) {
                Log.d(TAG, "All permission s granted");
                initializeApp();
            } else {
                Log.d(TAG, "Permissions not granted");
                Toast.makeText(this, "Permissions not granted. App cannot function properly.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void detect(float prox, float light, float[] g, int inc) {
        if (prox < 1 && light < 2) {
            Toast.makeText(this, "In Pocket", Toast.LENGTH_SHORT).show();
            fullLockdown();
        }
        if (prox >= 1 && light >= 2) {
            semiLockdown(g, inc);

        }
    }

    private void fullLockdown() {
        turnOffWifi();
        turnOffBluetooth();
    }

    private void turnOffWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
                Toast.makeText(this, "Wi-Fi off", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "Wi-Fi permission not granted");
            Toast.makeText(this, "Wi-Fi permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOffBluetooth() {
        BluetoothManager bluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
                Toast.makeText(this, "bluetooth off", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "Bluetooth permission not granted");
            Toast.makeText(this, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    private int getOriginalBrightnessLevel() {
        try {
            return Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.v("MainActivity", "Error retrieving default brightness level: " + e.getMessage(), e);
            return 255; // Default to maximum brightness if unable to retrieve default brightness level
        }
    }

    private void setBrightness(int brightness) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutParams);
    }

    private void semiLockdown(float[] g, int inc){
        if(inc < 10 || g[1] < 0.2 || g[1] > 0.9 || g[0] < -0.2 || g[0] > 0.2){
            makeScreenLookOff();
        } else if (inc < 30 || g[1] < 0.5 || g[1] > 0.8 || g[0] < -0.1 || g[0] > 0.1 ) {
            setBrightness(0);
        } else{
            setBrightness(originalBrightnessLevel);
            restoreScreen();
        }
    }
    private void makeScreenLookOff() {
        if (overlay == null) {
            overlay = new View(this);
            overlay.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            overlay.setBackgroundColor(Color.BLACK);
            ((FrameLayout) findViewById(android.R.id.content)).addView(overlay);
        }
        overlay.setVisibility(View.VISIBLE);
    }

    private void restoreScreen() {
        if (overlay != null) {
            overlay.setVisibility(View.GONE);
        }
        setBrightness(originalBrightnessLevel);
    }
}