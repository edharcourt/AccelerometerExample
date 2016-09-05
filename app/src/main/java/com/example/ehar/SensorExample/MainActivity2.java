package com.example.ehar.SensorExample;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by ehar on 8/15/16.
 */
public class MainActivity2
        extends AppCompatActivity
        implements Observer {

    // textviews
    private TextView accel_y_view = null;
    private Observable accel;
    private LocationHandler location;

    final public static int REQUEST_ASK_FINE_LOCATION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accel_y_view = (TextView) findViewById(R.id.accel_y);
        this.accel = new AccelerometerHandler(500, this);
        this.accel.addObserver(this);

        this.location = new LocationHandler(this);
        this.location.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object o) {

        if (observable instanceof AccelerometerHandler) {
            float[] values = (float[]) o;
            accel_y_view.setText(Float.toString(values[1]));
        }
        else if (observable instanceof LocationHandler) {
            Location l = (Location) o;
            double lat = l.getLatitude();
            double lon = l.getLongitude();

            Toast.makeText(MainActivity2.this, "Lat: " + lat +
                    " Lon: " + lon, Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ASK_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.i("INFO: ", "Permission not granted for location.");
            }
            else {
                Log.i("INFO: ", "Permission granted for location.");
                location.initializeLocationManager();
            }
    }
}
