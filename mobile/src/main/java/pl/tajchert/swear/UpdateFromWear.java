package pl.tajchert.swear;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import pl.tajchert.swearcommon.Tools;


public class UpdateFromWear extends WearableListenerService {
    /*
     * Service that handles all notification about data change between phone and a watch, however reacts
     * only to those dedicated to phone (requests of initial updates text).
     */
    private static final String TAG = "UpdateFromWear";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(TAG, "onMessageReceived message path: " + messageEvent.getPath() + ", data: " + messageEvent.getData());
        if(Tools.WEAR_ACTION_UPDATE.equals(messageEvent.getPath())) {
            String text = new String(messageEvent.getData());
            Log.d(TAG, "onMessageReceived our text: " + text);
            Intent mIntent = new Intent(this, UpdateService.class);
            UpdateFromWear.this.startService(mIntent);
        }
    }
}
