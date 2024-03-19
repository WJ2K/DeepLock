package com.example.deeplock

import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(),  Accelerometer.Listener {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Accelerometer
    private var isInPocket = false
    override fun onCreate(savedInstanceState: Bundle?) {
        accelerometer = Accelerometer(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        accelerometer = Accelerometer(this)
        accelerometer.setListener(this)
        accelerometer.register()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accelerometer.unregister()
    }

    override fun onTranslation(tx: Float, ty: Float, ts: Float) {
        if (!isInPocket && ty < -2.0f) { // Adjust this threshold as needed
            // The phone might be moved down to the pocket
            isInPocket = true
            Toast.makeText(this, "Phone moved down to pocket", Toast.LENGTH_SHORT).show()
        } else if (isInPocket && ty > 2.0f) { // Adjust this threshold as needed
            // The phone might be lifted out of the pocket
            isInPocket = false
            Toast.makeText(this, "Phone lifted out of pocket", Toast.LENGTH_SHORT).show()
        }
    }


}
