package pl.tajchert.swear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import pl.tajchert.swearcommon.Tools;


public class MainActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    private TextView textViewMainText;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewMainText = (TextView) findViewById(R.id.textViewMain);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        String swearText = MainActivity.this.getSharedPreferences(Tools.PREFS, Context.MODE_PRIVATE).getString(Tools.PREFS_KEY_SWEAR_TEXT, "Waiting for location and Internet access.");
        textViewMainText.setText(swearText);
        swipeRefreshLayout.setOnRefreshListener(this);

        Intent mIntent = new Intent(MainActivity.this, UpdateService.class);
        MainActivity.this.startService(mIntent);
        swipeRefreshLayout.setRefreshing(true);
    }

    public void onEvent(String swearText) {
        swipeRefreshLayout.setRefreshing(false);
        textViewMainText.setText(swearText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        Intent mIntent = new Intent(MainActivity.this, UpdateService.class);
        MainActivity.this.startService(mIntent);
    }
}
