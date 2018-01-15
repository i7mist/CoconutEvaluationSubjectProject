package me.tianshili.simpleweather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.UUID;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    LocationManager mLocationManager;
    WeatherManager mWeatherManager = null;
    String UserID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: create and add ad view (using test ad)

        // TODO: get current location and update the weather information with the location
        Location currentLocation = null;

        storeLocationData(currentLocation);
    }

    // This is a helper function which gets the up-to-date local weather info with the current location passed in, and update the main UI
    void fetchWeatherBasedOnLocation(Location currentLocation) {
        if (currentLocation != null) {
            if (mWeatherManager == null) {
                mWeatherManager = new WeatherManager(this);
            }
            mWeatherManager.update(currentLocation);
        } else {
            Toast.makeText(this, "Location is not available!", Toast.LENGTH_SHORT).show();
        }
    }

    void storeLocationData(Location currentLocation) {
        // TODO: fill this function to store current location data on the phone
    }

}
