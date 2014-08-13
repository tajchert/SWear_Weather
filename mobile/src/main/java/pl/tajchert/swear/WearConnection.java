package pl.tajchert.swear;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import pl.tajchert.swearcommon.Tools;


public class WearConnection {
    private final GoogleApiClient mGoogleAppiClient;
    private String TAG = WearConnection.class.getSimpleName();

    public  WearConnection(Context context) {
        mGoogleAppiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();

    }

    public void connect(){
        mGoogleAppiClient.connect();
    }

    public void disconnect() {
        mGoogleAppiClient.disconnect();
    }

    public void sendData(String key, String value) {
        if(value == null || value.length() < 3){
            //Empty string, do not send, 3 is for 'hot' word
            return;
        }
        Log.d(TAG, "Content to send: " + value);
        PutDataMapRequest dataMap = PutDataMapRequest.create(Tools.WEAR_PATH);
        dataMap.getDataMap().putString(key, value);
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleAppiClient, request);
        Log.d(TAG, "Trying to send: " + value);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.d(TAG, "Sent: " + dataItemResult.toString());
            }
        });

    }

}
