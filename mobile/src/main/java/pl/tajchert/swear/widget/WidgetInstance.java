package pl.tajchert.swear.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import pl.tajchert.swear.UpdateService;
import pl.tajchert.swearcommon.Tools;

/**
 * Created by tajchert on 18.04.15.
 */
public class WidgetInstance extends AppWidgetProvider {
    private static final String TAG = "WidgetInstance";
    public int id;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent mIntent = new Intent(context, UpdateService.class);
        context.startService(mIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Tools.PREFS, Context.MODE_PRIVATE);
        String swearText = sharedPreferences.getString(Tools.PREFS_KEY_SWEAR_TEXT, "Waiting for location");
        long timeDiff = (System.currentTimeMillis() - sharedPreferences.getLong(Tools.PREFS_KEY_TIME_LAST_UPDATE, 0));
        if(timeDiff > 3000) {
            Intent mIntent = new Intent(context, UpdateService.class);
            context.startService(mIntent);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for(int widgetId: appWidgetIds) {
            Widget.updateAppWidget(swearText, context, appWidgetManager, widgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}
