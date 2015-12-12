package pl.tajchert.swear;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

import de.greenrobot.event.EventBus;
import pl.tajchert.swear.api.IWeatherAPI;
import pl.tajchert.swear.api.WeatherAPI;
import pl.tajchert.swear.widget.WidgetInstance;
import pl.tajchert.swearcommon.Tools;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateService extends Service {
    private static final String TAG = UpdateService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Location loc = getLastLocation();
        getAPIContent(loc);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    private Location getLastLocation() {
        LocationManager locationManager = (LocationManager) UpdateService.this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        return lastKnownLocation;
    }

    public void getAPIContent(Location location){
        if(location == null){
            new SendWeatherTextTask().execute();
            return;
        }
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5/")
                .build();
        IWeatherAPI weatherService = restAdapter.create(IWeatherAPI.class);
        weatherService.getWeather(Double.parseDouble(String.format("%.1f", location.getLatitude())), Double.parseDouble(String.format("%.1f", location.getLongitude())), "93a83c9ecade00fc1315d6008fcd14c4", new Callback<WeatherAPI>() {
            @Override
            public void success(WeatherAPI weatherAPIs, Response response) {
                Log.d(TAG, "Success with downloading weather");
                new SendWeatherTextTask().execute(weatherAPIs);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.d(TAG, "Failure: " + retrofitError.toString());
            }
        });
    }

    private class SendWeatherTextTask extends AsyncTask<WeatherAPI, Void, String> {
        private GoogleApiClient mGoogleAppiClient;
        @Override
        protected String doInBackground(WeatherAPI... params) {
            if(params != null && params.length > 0){
                WeatherAPI currentWeather = params[0];
                if(currentWeather == null){
                    return null;
                }
                    return WeatherToTextConverter.getText(UpdateService.this, currentWeather);
            } else {

                return UpdateService.this.getString(R.string.swear_error);
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if(result == null || "".equals(result)){
                result = UpdateService.this.getString(R.string.swear_error);
            }
            //Widgets
            SharedPreferences.Editor edit = UpdateService.this.getSharedPreferences(Tools.PREFS, Context.MODE_PRIVATE).edit();
            edit.putString(Tools.PREFS_KEY_SWEAR_TEXT, result).commit();
            edit.putLong(Tools.PREFS_KEY_TIME_LAST_UPDATE, System.currentTimeMillis()).commit();
            updateWidgets();

            //Activity
            EventBus.getDefault().post(result);

            //Wear
            sendData(Tools.WEAR_KEY_SWEAR_TEXT, result);

            //TV?
            //Auto?
        }
        @Override
        protected void onPreExecute() {
            mGoogleAppiClient = new GoogleApiClient.Builder(UpdateService.this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle connectionHint) {
                        }
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                        }
                    })
                    .addApi(Wearable.API)
                    .build();
            mGoogleAppiClient.connect();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        private void sendData(String key, String value) {
            if(value == null || value.length() < 3){
                //Empty string, do not send, 3 is for 'hot' word
                return;
            }
            value = value +  Calendar.getInstance().getTimeInMillis();
            PutDataMapRequest dataMap = PutDataMapRequest.create(Tools.WEAR_PATH);
            dataMap.getDataMap().putString(key, value);
            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleAppiClient, request);
            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sent: " + dataItemResult.toString());
                    mGoogleAppiClient.disconnect();
                    stopSelf();
                }
            });

        }
    }

    private void updateWidgets() {
        Intent intent = new Intent(this, WidgetInstance.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetInstance.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }


}
