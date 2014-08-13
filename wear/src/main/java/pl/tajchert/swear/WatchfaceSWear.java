package pl.tajchert.swear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import pl.tajchert.swearcommon.Tools;


public class WatchfaceSWear extends Activity {
    private static final String TAG = WatchfaceSWear.class.getSimpleName();
    private BroadcastReceiver dataChangedReceiver;
    private IntentFilter dataChangedIntentFilter;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ImageView imageClockFace = (ImageView) findViewById(R.id.imageViewClockFace);
        imageClockFace.setImageDrawable(getResources().getDrawable(R.drawable.classic_background_one));

        dataChangedIntentFilter = new IntentFilter(Tools.DATA_CHANGED_ACTION);
        dataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "DataChangedReceived: "+ intent.getAction());
                if (Tools.DATA_CHANGED_ACTION.equals(intent.getAction())) {
                    String lastLocationText = WatchfaceSWear.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_LAST_LOCATION, "");
                    lastLocation = Tools.locationFromString(lastLocationText);
                    Log.d(TAG, "Location: " + lastLocationText);
                    Log.d(TAG, "Location: " + lastLocation);
                    //imageOverflow.setImageBitmap(drawEvents(eventsSet));
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(dataChangedReceiver, dataChangedIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(dataChangedReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //imageOverflow.setImageResource(R.drawable.kit_kat_hand_dial);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
