package pl.tajchert.swear;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import pl.tajchert.swearcommon.Tools;


/**
 * Created by tajchert on 07.04.15.
 */
public class SendStringToNode extends Thread {
    private static final String TAG = "SendStringToNode";
    private byte[] objectArray;
    private Context context;
    private String path;

    public SendStringToNode(String textToSend, Context ctx) {
        this(Tools.MESSAGE_PATH_MAIN, textToSend, ctx);
    }

    public SendStringToNode(Context ctx) {
        this(Tools.WEAR_ACTION_UPDATE, "update_request" + Calendar.getInstance().getTimeInMillis(), ctx);
    }

    public SendStringToNode(String messagePath, String textToSend, Context ctx) {
        context = ctx;
        path = messagePath;
        if(textToSend != null){
            objectArray = textToSend.getBytes();
        } else {
            objectArray = "".getBytes();
        }
    }

    public void run() {
        if ((objectArray.length / 1024) > 100) {
            throw new RuntimeException("Object is too big to push it via Google Play Services");
        }
        GoogleApiClient googleApiClient = GoogleApiManager.getInstance(context);
        googleApiClient.blockingConnect(200, TimeUnit.MILLISECONDS);
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        for (Node node : nodes.getNodes()) {
            MessageApi.SendMessageResult result;
            result = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, objectArray).await();
            if (!result.getStatus().isSuccess()) {
                Log.v(TAG, "ERROR: failed to send Message via Google Play Services");
            }
        }
    }
}