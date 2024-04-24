package com.example.deeplock;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    SensorListener sensorListener;
    Context context = this;

    int pocket = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorListener = new SensorListener(this, context);

    }

    public void detect(float prox, float light, float[] g, int inc) {
        if ((prox < 1) && (light < 2) && (g[1] < -0.6) && ((inc > 75) || (inc < 100))) {
            pocket = 1;
        }
        if ((prox >= 1) && (light >= 2) && (g[1] >= -0.7)) {
            if (pocket == 1) {
                pocket = 0;
            }
        }
    }


}