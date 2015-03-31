package pl.tajchert.swear;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private static final String androidWearURL = "http://www.android.com/wear/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonAndroidWear = (Button) findViewById(R.id.buttonAndroidWear);
        buttonAndroidWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(androidWearURL));
                startActivity(i);
            }
        });
    }
}
