package pl.tajchert.swear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import pl.tajchert.swearcommon.Tools;


public class WatchfaceSWear extends Activity {
    private static final String TAG = WatchfaceSWear.class.getSimpleName();
    private BroadcastReceiver dataChangedReceiver;
    private IntentFilter dataChangedIntentFilter;
    private TextView swearContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        swearContainer = (TextView) findViewById(R.id.TextViewSwearContainer);
        dataChangedIntentFilter = new IntentFilter(Tools.DATA_CHANGED_ACTION);
        swearContainer.setText("onCreate");
        dataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "DataChangedReceived: "+ intent.getAction());
                swearContainer.setText("Received");
                if (Tools.DATA_CHANGED_ACTION.equals(intent.getAction())) {
                    String swearText = WatchfaceSWear.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_KEY_SWEAR_TEXT, "got null");
                    if(swearText == null){
                        return;
                    }
                    Log.d(TAG, "Swear got: " + swearText);
                    swearContainer.setText(swearText);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
