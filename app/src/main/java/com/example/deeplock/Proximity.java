package com.example.deeplock;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Proximity {
    public interface Listener {
        void onProximityChange(float distance);
    }
    private Listener listener;

    public void setListener(Listener l) {
        listener = l;
    }

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximityEventListener;

    // Create constructor with context as argument
    Proximity(Context context) {

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        proximityEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (listener != null) {
                    listener.onProximityChange(sensorEvent.values[0]);
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                //
            }
        };
    }

//    public void register() {
//        sensorManager.registerListener(proximityEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
//    }
//
//    public void unregister() {
//        sensorManager.unregisterListener(proximityEventListener);
//    }

}
