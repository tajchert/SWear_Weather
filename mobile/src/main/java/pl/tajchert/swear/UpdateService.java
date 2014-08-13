package pl.tajchert.swear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

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
    private WearConnection connection;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        connection = new WearConnection(getBaseContext());
        connection.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connection.disconnect();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Location loc = getLastLocation();
        if(loc != null){
            getAPI(loc);
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

    public void getAPI(Location location){
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
            connection.sendData(Tools.WEAR_KEY_SWEAR_TEXT, result);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
