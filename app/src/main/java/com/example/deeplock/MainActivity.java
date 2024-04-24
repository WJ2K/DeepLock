package com.example.deeplock;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;


public class MainActivity extends Activity  {

    SensorListener sensorListener;
    Context context = this;
    MediaPlayer mp[] = new MediaPlayer[12];

    int pocket = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sensorListener = new SensorListener(this, context);

        prepare();

    }

    public void detect(float prox, float light, float g[], int inc){
        if((prox<1)&&(light<2)&&(g[1]<-0.6)&&( (inc>75)||(inc<100))){
            pocket=1;
        }
        if((prox>=1)&&(light>=2)&&(g[1]>=-0.7)){
            if(pocket==1){
                playSound();
                pocket=0;
            }
        }
    }