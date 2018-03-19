package me.tianshili.simpleweather;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;

import me.tianshili.annotationlib.network.NetworkAnnotation;

/**
 * Created by tianshi on 1/13/18.
 */

public class WeatherManager {
    private String TAG = "WeatherManager";
    private RequestQueue mRequestQueue;
    private Activity mActivity;
    private TextView mLocationNameTextView;
    private TextView mWeatherDescriptionTextView;
    private TextView mTemperatureTextView;
    private TextView mHumidityTextView;
    private TextView mCloudinessTextView;
    private TextView mSunriseTimeTextView;
    private TextView mSunsetTimeTextView;

    WeatherManager(Activity activity) {
        mActivity = activity;

        mLocationNameTextView = mActivity.findViewById(R.id.location_name);
        mWeatherDescriptionTextView = mActivity.findViewById(R.id.weather_description);
        mTemperatureTextView = mActivity.findViewById(R.id.temperature);
        mHumidityTextView = mActivity.findViewById(R.id.humidity);
        mCloudinessTextView = mActivity.findViewById(R.id.cloudiness);
        mSunriseTimeTextView = mActivity.findViewById(R.id.sunrise_time);
        mSunsetTimeTextView = mActivity.findViewById(R.id.sunset_time);

        Cache cache = new DiskBasedCache(mActivity.getCacheDir(), 1024 * 1024); // 1MB cap

        Network network = new BasicNetwork(new HurlStack());

        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    }

    // fetch local weather based on the current location passed in
    public void update(Location currentLocation) {
        String currentWeatherURL = mActivity.getString(R.string.current_weather_url_str,
                currentLocation.getLatitude(),
                currentLocation.getLongitude());
        Log.d(TAG, "currentWeatherURL: " + currentWeatherURL);
        @NetworkAnnotation(
                destination = {"https://openweathermap.org"},
                encryptedInTransmission = {false},
                purposeDescription = {"For automatically fetching local weather information"},
                retentionTime = {"According to the privacy policy of OpenWeatherMap: Personal information that we process for any purpose or purposes shall not be kept for longer than is necessary for that purpose or those purposes. (https://openweathermap.org/privacy-policy)"}) JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, currentWeatherURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    String weatherDescription = response.getJSONArray("weather").getJSONObject(0).getString("description");
                    Double temperatureK = response.getJSONObject("main").getDouble("temp");
                    int temperatureF = Double.valueOf(((temperatureK - 273) * 9.0/5) + 32).intValue();
                    int humidity = response.getJSONObject("main").getInt("humidity"); // %
                    int cloudiness = response.getJSONObject("clouds").getInt("all"); // %
                    Long sunriseTime = response.getJSONObject("sys").getLong("sunrise"); // UNIX time
                    Long sunsetTime = response.getJSONObject("sys").getLong("sunset"); // UNIX time
                    Calendar sunriseParsedTime = Calendar.getInstance();
                    sunriseParsedTime.setTimeInMillis(sunriseTime * 1000);
                    int sunriseHour = sunriseParsedTime.get(Calendar.HOUR_OF_DAY);
                    int sunriseMinute = sunriseParsedTime.get(Calendar.MINUTE);
                    Calendar sunsetParsedTime = Calendar.getInstance();
                    sunsetParsedTime.setTimeInMillis(sunsetTime * 1000);
                    int sunsetHour = sunsetParsedTime.get(Calendar.HOUR_OF_DAY);
                    int sunsetMinute = sunsetParsedTime.get(Calendar.MINUTE);
                    String country = response.getJSONObject("sys").getString("country");
                    String city = response.getString("name");

                    mLocationNameTextView.setText(mActivity.getString(R.string.location_name_pattern, city, country));
                    mWeatherDescriptionTextView.setText(weatherDescription);
                    mTemperatureTextView.setText(mActivity.getString(R.string.temperature_pattern, temperatureF));
                    mHumidityTextView.setText(mActivity.getString(R.string.humidity_pattern, humidity));
                    mCloudinessTextView.setText(mActivity.getString(R.string.cloudiness_pattern, cloudiness));
                    mSunriseTimeTextView.setText(mActivity.getString(R.string.time_of_day_pattern, sunriseHour, sunriseMinute));
                    mSunsetTimeTextView.setText(mActivity.getString(R.string.time_of_day_pattern, sunsetHour, sunsetMinute));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueue.add(request);
    }
}
