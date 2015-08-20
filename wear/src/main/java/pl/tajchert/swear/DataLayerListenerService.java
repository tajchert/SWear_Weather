package pl.tajchert.swear;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.tajchert.swearcommon.Tools;


public class DataLayerListenerService extends WearableListenerService {
    private static final String TAG = "DataLayerService";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        //dataEvents.close();
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            Log.d(TAG, "Update, path: " +path);
            if (Tools.WEAR_PATH.equals(path)) {
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                String weatherText = item.getDataMap().getString(Tools.WEAR_KEY_SWEAR_TEXT);
                Log.d(TAG, "got: " + weatherText);
                if(weatherText != null) {
                    weatherText = weatherText.replaceAll("\\d","");
                    if(!weatherText.equals("")){
                        getBaseContext().getSharedPreferences(Tools.PREFS, MODE_PRIVATE).edit().putString(Tools.PREFS_KEY_SWEAR_TEXT, weatherText).commit();
                        getBaseContext().sendBroadcast(new Intent(Tools.DATA_CHANGED_ACTION));
                    }
                }
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {}

    @Override
    public void onPeerDisconnected(Node peer){}

}
