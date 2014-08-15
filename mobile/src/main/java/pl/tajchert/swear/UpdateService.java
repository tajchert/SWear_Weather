package pl.tajchert.swear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import java.util.List;

import pl.tajchert.swear.api.IWeatherAPI;
import pl.tajchert.swear.api.WeatherAPI;
import pl.tajchert.swearcommon.Tools;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpdateService extends Service {
    private static final String TAG = UpdateService.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Location loc = getLastLocation();
        if(loc != null){
            getAPIContent(loc);
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private Location getLastLocation() {
        LocationManager lm = (LocationManager) UpdateService.this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);
        Location best = null;

        for (int i=providers.size()-1; i>=0; i--) {
            if(best == null || (lm.getLastKnownLocation(providers.get(i)) != null && best.getAccuracy() > lm.getLastKnownLocation(providers.get(i)).getAccuracy())){
                best = lm.getLastKnownLocation(providers.get(i));
            }
        }
        return best;
    }

    public void getAPIContent(Location location){
        if(location == null){
            return;
        }
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://api.openweathermap.org/data/2.5/")
                .build();
        IWeatherAPI weatherService = restAdapter.create(IWeatherAPI.class);
        weatherService.getWeather(location.getLatitude(), location.getLongitude(), new Callback<WeatherAPI>() {
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
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if(result == null){
                return;
            }
            sendData(Tools.WEAR_KEY_SWEAR_TEXT, result);

        }
        @Override
        protected void onPreExecute() {
            mGoogleAppiClient = new GoogleApiClient.Builder(UpdateService.this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle connectionHint) {
                            Log.d(TAG, "onConnected");
                        }
                        @Override
                        public void onConnectionSuspended(int cause) {
                            Log.d(TAG, "onConnectionSuspended");
                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.d(TAG, "onConnectionFailed");
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
            Log.d(TAG, "Content to send: " + value);
            value = value +  Calendar.getInstance().getTimeInMillis();
            PutDataMapRequest dataMap = PutDataMapRequest.create(Tools.WEAR_PATH);
            dataMap.getDataMap().putString(key, value);
            PutDataRequest request = dataMap.asPutDataRequest();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleAppiClient, request);
            Log.d(TAG, "Trying to send: " + value);
            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    Log.d(TAG, "Sent: " + dataItemResult.toString());
                    mGoogleAppiClient.disconnect();
                }
            });

        }
    }


}
