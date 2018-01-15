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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location currentLocation;
            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            fetchWeatherBasedOnLocation(currentLocation);

            storeLocationData(currentLocation);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create and add ad view (using test ad)
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        // get current location
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        if (mLocationManager != null) {
            Location currentLocation;
            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            fetchWeatherBasedOnLocation(currentLocation);

            storeLocationData(currentLocation);
        }
    }

    void fetchWeatherBasedOnLocation(Location currentLocation) {
        // get up-to-date local weather info
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
        if (currentLocation != null) {
            String filename = getString(R.string.location_filename_pattern, getUserID());
            try {
                FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE|Context.MODE_APPEND);
                Gson gson = new Gson();
                fos.write(gson.toJson(currentLocation).getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUserID() {
        if (UserID != null) {
            Log.d(TAG, "User ID: " + UserID);
            return UserID;
        }
        SharedPreferences user_profile = getSharedPreferences("user_profile", 0);
        SharedPreferences.Editor editor = user_profile.edit();
        if (user_profile.getString("user_id", null) == null) {
            UserID = UUID.randomUUID().toString();
            editor.putString("user_id", UserID);
            editor.apply();
        } else {
            UserID = user_profile.getString("user_id", null);
        }
        Log.d(TAG, "User ID: " + UserID);
        return UserID;
    }
}
