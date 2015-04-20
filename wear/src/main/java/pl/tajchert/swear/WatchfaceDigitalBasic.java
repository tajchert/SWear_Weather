package pl.tajchert.swear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.util.Calendar;

import pl.tajchert.swearcommon.Tools;

public class WatchfaceDigitalBasic extends CanvasWatchFaceService {

    private boolean isRound;
    private int screenWidthPX;
    private int screenHeightPX;



    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {

        final BroadcastReceiver dataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Tools.DATA_CHANGED_ACTION.equals(intent.getAction())) {
                    WatchfaceDigitalBasic.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).edit().putLong(Tools.PREFS_KEY_TIME_LAST_UPDATE, Calendar.getInstance().getTimeInMillis()).apply();
                    invalidate();
                }
            }
        };


        Paint mBackgroundPaint;
        TextPaint mSwearPaint;

        boolean mAmbient;


        float mYOffset;
        float mYOffsetText;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchfaceDigitalBasic.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setShowSystemUiTime(true)
                    .setHotwordIndicatorGravity(Gravity.TOP | Gravity.RIGHT)
                    .setStatusBarGravity(Gravity.RIGHT | Gravity.TOP)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .build());
            Resources resources = WatchfaceDigitalBasic.this.getResources();
            WindowManager wm = (WindowManager) WatchfaceDigitalBasic.this.getSystemService(Context.WINDOW_SERVICE);
            getScreenSize(wm);
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);
            mYOffsetText = resources.getDimension(R.dimen.digital_y_offset_text);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(Color.BLACK);

            mSwearPaint = new TextPaint();
            mSwearPaint.setTextSize(60);
            mSwearPaint.setColor(Color.WHITE);
            mSwearPaint.setAntiAlias(true);
            mSwearPaint.setSubpixelText(true);
            mSwearPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));

            registerReceiver();
            new SendStringToNode(WatchfaceDigitalBasic.this).start();
        }

        @Override
        public void onDestroy() {
            unregisterReceiver();
            new SendStringToNode("STOP", WatchfaceDigitalBasic.this).start();
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                if( WatchfaceDigitalBasic.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getLong(Tools.PREFS_KEY_TIME_LAST_UPDATE, Calendar.getInstance().getTimeInMillis()) - System.currentTimeMillis() > 900000) {
                    new SendStringToNode(WatchfaceDigitalBasic.this).start();
                }
                //registerReceiver();
            } else {
                //unregisterReceiver();
            }
        }

        private void registerReceiver() {
            IntentFilter dataChangedIntentFilter = new IntentFilter(Tools.DATA_CHANGED_ACTION);
            WatchfaceDigitalBasic.this.registerReceiver(dataChangedReceiver, dataChangedIntentFilter);
        }

        private void unregisterReceiver() {
            WatchfaceDigitalBasic.this.unregisterReceiver(dataChangedReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = WatchfaceDigitalBasic.this.getResources();
            isRound = insets.isRound();

            mYOffsetText = resources.getDimension(isRound ? R.dimen.digital_y_offset_text_round : R.dimen.digital_y_offset_text);

        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mSwearPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw the background.
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            String swearText = WatchfaceDigitalBasic.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_KEY_SWEAR_TEXT, "got null");
            if(swearText == null){
                return;
            }
            TextSizeHelper.timeStart = System.currentTimeMillis();
            if(isRound) {
                mSwearPaint.setTextSize(TextSizeHelper.getAutofitTextSize(swearText, mSwearPaint, (screenWidthPX - 130), 2, 30, 55, 5, WatchfaceDigitalBasic.this.getResources().getDisplayMetrics()));
            } else {
                mSwearPaint.setTextSize(TextSizeHelper.getAutofitTextSize(swearText, mSwearPaint, screenWidthPX, 3, 35, 65, 5, WatchfaceDigitalBasic.this.getResources().getDisplayMetrics()));
            }

            StaticLayout layout = new StaticLayout(swearText, mSwearPaint, screenWidthPX, Layout.Alignment.ALIGN_CENTER, 1.0f, 0, false);
            canvas.translate(0, mYOffsetText); //position the text
            layout.draw(canvas);
        }
    }

    private void getScreenSize(WindowManager wm) {
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidthPX = size.x;
        screenHeightPX = size.y;
    }
}
