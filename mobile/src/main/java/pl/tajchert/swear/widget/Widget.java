package pl.tajchert.swear.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;

import pl.tajchert.swear.R;

/**
 * Created by tajchert on 18.04.15.
 */
public class Widget {
    private static final String TAG = "Widget";

    public static void updateAppWidget(String swearText, Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        PendingIntent pendingIntent;
        RemoteViews views;
        views = new RemoteViews(context.getPackageName(), R.layout.widget_basic_medium);
        swearText = "It is severely fucking gale-force windy.";
        int textSize = (int) Math.floor(35-(swearText.length()*1.2-15));
        if(swearText.length() > 25) {
            textSize = 20;
        }
        views.setFloat(R.id.textView, "setTextSize", textSize);
        views.setTextViewText(R.id.textView, swearText);
        pendingIntent = PendingIntent.getActivity(context, 0, getWeatherAppIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.mainLayout, pendingIntent);
        views.setOnClickPendingIntent(R.id.textView, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    public static Intent getWeatherAppIntent(Context context) {
        String [] appPackageNames = {"com.google.android.apps.genie.geniewidget", "com.accuweather.android", "com.devexpert.weather", "com.weather.Weather", "com.yahoo.mobile.client.android.weather", "com.macropinch.swan"};
        PackageManager manager = context.getPackageManager();
        for(String packageName : appPackageNames) {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if(i != null) {
                return i;
            }
        }
        return null;
    }
}
