package pl.tajchert.swear;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import pl.tajchert.swearcommon.Tools;


public class UpdateFromWear extends WearableListenerService {
    /*
     * Service that handles all notification about data change between phone and a watch, however reacts
     * only to those dedicated to phone (requests of initial updates text).
     */
    private static final String TAG = "UpdateFromWear";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (Tools.WEAR_PATH_ACTION_UPDATE.equals(path)) {
                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                String weatherText = item.getDataMap().getString(Tools.WEAR_ACTION_UPDATE);
                Log.d(TAG, "onDataChanged got from phone: " + weatherText);
                Intent mIntent = new Intent(this, UpdateService.class);
                UpdateFromWear.this.startService(mIntent);
            }
        }
    }
}
