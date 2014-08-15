package pl.tajchert.swear;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import pl.tajchert.swearcommon.Tools;


public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = DataLayerListenerService.class.getSimpleName();
    GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        Log.d(TAG, "SWear onCreate");
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            Log.d(TAG, "Update, path: " +path);
            if (Tools.WEAR_PATH.equals(path)) {
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                String weatherText = item.getDataMap().getString(Tools.WEAR_KEY_SWEAR_TEXT);



                if(weatherText != null) {
                    weatherText = weatherText.replaceAll("\\d","");
                    String oldWeatherText = getBaseContext().getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_KEY_SWEAR_TEXT, "");
                    if(oldWeatherText.equals(weatherText)){
                        return;
                    }

                    if(!weatherText.equals("")){
                        Log.d(TAG, "weatherText: " + weatherText);
                        getBaseContext().getSharedPreferences(Tools.PREFS, MODE_PRIVATE).edit().putString(Tools.PREFS_KEY_SWEAR_TEXT, weatherText).commit();
                        getBaseContext().sendBroadcast(new Intent(Tools.DATA_CHANGED_ACTION));
                    }
                }
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "onPeerDisconnected: " + peer);
    }

}
