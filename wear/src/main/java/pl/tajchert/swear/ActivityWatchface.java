package pl.tajchert.swear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

import pl.tajchert.swearcommon.Tools;


public class ActivityWatchface extends Activity {
    private static final String TAG = ActivityWatchface.class.getSimpleName();
    private BroadcastReceiver dataChangedReceiver;
    private IntentFilter dataChangedIntentFilter;
    private AutoSizeTextView swearContainer;

    private ImageView refreshCircle;
    private Animation refreshAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        refreshCircle = (ImageView) findViewById(R.id.refreshCircle);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                swearContainer = (AutoSizeTextView) stub.findViewById(R.id.TextViewSwearContainer);
                refreshAnim = AnimationUtils.loadAnimation(ActivityWatchface.this, R.anim.refresh_animation);
                if(refreshAnim != null){
                    refreshAnimation(refreshAnim, ActivityWatchface.this.getString(R.string.swear_null));
                }
            }
        });
        dataChangedIntentFilter = new IntentFilter(Tools.DATA_CHANGED_ACTION);
        sendNotificationToMobile();

        dataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Tools.DATA_CHANGED_ACTION.equals(intent.getAction())) {
                    String swearText = ActivityWatchface.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_KEY_SWEAR_TEXT, "got null");
                    if(swearText == null || swearContainer == null){
                        return;
                    }
                    if(refreshAnim != null && !swearContainer.getText().equals(swearText)){
                        refreshAnimation(refreshAnim, swearText);
                    }
                    ActivityWatchface.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).edit().putLong(Tools.PREFS_KEY_TIME_LAST_UPDATE, Calendar.getInstance().getTimeInMillis()).commit();
                }
            }
        };
    }

    private void sendNotificationToMobile(){
        //Send empty string to ask phone to refresh weather data
        Log.d(TAG, "sendNotificationToMobile ");
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
        String value = "update_request";
        value = value +  Calendar.getInstance().getTimeInMillis();
        PutDataMapRequest dataMap = PutDataMapRequest.create(Tools.WEAR_PATH_ACTION_UPDATE);
        dataMap.getDataMap().putString(Tools.WEAR_ACTION_UPDATE, value);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "Sent: " + dataItemResult.toString());
                googleApiClient.disconnect();
            }
        });
    }

    private boolean timeToRefresh(){
        if(swearContainer != null && swearContainer.getText() != null){
            if( swearContainer.getText().equals(ActivityWatchface.this.getString(R.string.swear_null)) || swearContainer.getText().equals(ActivityWatchface.this.getString(R.string.swear_null))){
                return true;
            }

        }
        return Calendar.getInstance().getTimeInMillis() - ActivityWatchface.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getLong(Tools.PREFS_KEY_TIME_LAST_UPDATE, 0) > (Tools.REFRESH_INTERVAL);
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
    protected void onResume() {
        super.onResume();
        if(timeToRefresh()){
            sendNotificationToMobile();
        }
    }

    private void refreshAnimation(final Animation animation, final String textToShow){
        refreshCircle.setVisibility(View.VISIBLE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (swearContainer != null) {
                            swearContainer.setText(textToShow);
                        }
                    }
                }, (animation.getDuration() + 300));
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                refreshCircle.setVisibility(View.GONE);
            }
        });
        refreshCircle.startAnimation(animation);
    }
}
