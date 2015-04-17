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
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.format.Time;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.view.WindowManager;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import pl.tajchert.swearcommon.Tools;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class WatchfaceDigitalBasic extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    private IntentFilter dataChangedIntentFilter;
    private boolean isRound;
    private int screenWidthPX;
    private int screenHeightPX;

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(60);

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        static final int MSG_UPDATE_TIME = 0;

        /**
         * Handler to update the time periodically in interactive mode.
         */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };

        final BroadcastReceiver dataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Tools.DATA_CHANGED_ACTION.equals(intent.getAction())) {
                    WatchfaceDigitalBasic.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).edit().putLong(Tools.PREFS_KEY_TIME_LAST_UPDATE, Calendar.getInstance().getTimeInMillis()).apply();
                    invalidate();
                }
            }
        };

        boolean mRegisteredTimeZoneReceiver = false;

        Paint mBackgroundPaint;
        Paint mTextPaint;
        TextPaint mSwearPaint;

        boolean mAmbient;

        Time mTime;

        float mXOffset;
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
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setShowSystemUiTime(true)
                    .setHotwordIndicatorGravity(Gravity.RIGHT)
                    .setStatusBarGravity(Gravity.RIGHT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                            //.setShowSystemUiTime(false)
                    .build());
            Resources resources = WatchfaceDigitalBasic.this.getResources();
            WindowManager wm = (WindowManager) WatchfaceDigitalBasic.this.getSystemService(Context.WINDOW_SERVICE);
            getScreenSize(wm);
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);
            mYOffsetText = resources.getDimension(R.dimen.digital_y_offset_text);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.digital_background));

            mTextPaint = new Paint();
            mTextPaint = createTextPaint(resources.getColor(R.color.digital_text));
            mSwearPaint = new TextPaint();
            mSwearPaint.setTextSize(60);
            mSwearPaint.setColor(Color.WHITE);
            mSwearPaint.setAntiAlias(true);
            mSwearPaint.setSubpixelText(true);
            mSwearPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC));

            mTime = new Time();

            new SendStringToNode(WatchfaceDigitalBasic.this).start();
        }

        @Override
        public void onDestroy() {
            new SendStringToNode("STOP", WatchfaceDigitalBasic.this).start();
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            IntentFilter dataChangedIntentFilter = new IntentFilter(Tools.DATA_CHANGED_ACTION);
            WatchfaceDigitalBasic.this.registerReceiver(mTimeZoneReceiver, filter);
            WatchfaceDigitalBasic.this.registerReceiver(dataChangedReceiver, dataChangedIntentFilter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            } else {
                WatchfaceDigitalBasic.this.unregisterReceiver(mTimeZoneReceiver);
                mRegisteredTimeZoneReceiver = false;
            }
            WatchfaceDigitalBasic.this.unregisterReceiver(dataChangedReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = WatchfaceDigitalBasic.this.getResources();
            isRound = insets.isRound();
            //mXOffset = resources.getDimension(isRound ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);

            mTextPaint.setTextSize(textSize);
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
                    mTextPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw the background.
            canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            mTime.setToNow();
            String text = String.format("%d:%02d", mTime.hour, mTime.minute);
            if(isRound){
                mXOffset = (screenWidthPX - mTextPaint.getTextSize() * Math.abs(text.length() / 2)) / 2;
            } else {
                mXOffset = (screenWidthPX - (mTextPaint.measureText(text))- getResources().getDimension(R.dimen.digital_x_offset));
            }
            //canvas.drawText(text, mXOffset, mYOffset, mTextPaint);
            String swearText = WatchfaceDigitalBasic.this.getSharedPreferences(Tools.PREFS, MODE_PRIVATE).getString(Tools.PREFS_KEY_SWEAR_TEXT, "got null");
            if(swearText == null){
                return;
            }
            mSwearPaint.setTextSize(TextSizeHelper.getAutofitTextSize(swearText, mSwearPaint, screenWidthPX, 3, 38, 70, 4, WatchfaceDigitalBasic.this.getResources().getDisplayMetrics()));

            StaticLayout layout = new StaticLayout(swearText, mSwearPaint, screenWidthPX, Layout.Alignment.ALIGN_CENTER, 1.3f, 0, false);
            canvas.translate(0, mYOffsetText); //position the text
            layout.draw(canvas);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
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
