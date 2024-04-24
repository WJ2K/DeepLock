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
    private int pocket = 0;

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
        // Initialize other components or operations that require the granted permissions
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
                Log.d(TAG, "All permissions granted");
                initializeApp();
            } else {
                Log.d(TAG, "Permissions not granted");
                Toast.makeText(this, "Permissions not granted. App cannot function properly.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void detect(float prox, float light, float[] g, int inc) {
        if (prox < 1 && light < 2 && g[1] < -0.6 && inc >= 76 && inc <= 99) {
            pocket = 1;
            fullLockdown();
        }
        if (prox >= 1 && light >= 2 && g[1] >= -0.7) {
            if (pocket == 1) {
                pocket = 0;
            }
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
            }
        } else {
            Log.d(TAG, "Bluetooth permission not granted");
            Toast.makeText(this, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
}