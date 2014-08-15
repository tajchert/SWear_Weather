package pl.tajchert.swear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class ActivityMain extends Activity {
    private static final String TAG = ActivityMain.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent = new Intent(this, UpdateService.class);
        ActivityMain.this.startService(mIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
