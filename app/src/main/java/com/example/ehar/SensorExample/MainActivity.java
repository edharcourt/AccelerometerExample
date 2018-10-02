package com.example.ehar.SensorExample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity
                        extends Activity
                        implements SensorEventListener,
                                   LocationListener {

    // textviews
    TextView accel_y_view = null;

    // accelerometer crud
    Sensor accelerometer = null;
    SensorManager sensorManager = null;
    long prev_time = 0;
    LocationManager lm;
    final private int REQUEST_ASK_FINE_LOCATION = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accel_y_view = (TextView) findViewById(R.id.accel_y);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                               PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ASK_FINE_LOCATION
                    );
        }
        else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }

    }  // onCreate

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ASK_FINE_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                try {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
                }
                catch(SecurityException e) {
                    return;  // should not get here
                }
                catch(Exception e) {
                    return;  // should not get here
                }
            }
            else {
                return; // :-( permission not granted
            }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        prev_time = System.currentTimeMillis();

            Location location = getLastKnownLocation();
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                Toast.makeText(MainActivity.this, "Lat: " + lat +
                        " Lon: " + lon, Toast.LENGTH_LONG).show();
            }
    }


    /**
     * Borrowed from StackOverflow
     * http://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null
     * @return
     */
    private Location getLastKnownLocation() {
        lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location bestLocation = null;
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location l = lm.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        // don't really need to check this because this is the only sensor registered
        if (sensorEvent.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        // must be the accelerometer - number of milliseconds since Midnight, Jan 1, 1970
        // How many milliseconds in a day? 60(60)(24)(1000) = 90,000,000
        long curr_time = System.currentTimeMillis();

        if (curr_time - prev_time > 500) {
            prev_time = curr_time;
            float y = sensorEvent.values[1];
            accel_y_view.setText(Float.toString(y));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }

    @Override
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        Toast.makeText(MainActivity.this, "Lat: " + lat +
                                          " Lon: " + lon, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
