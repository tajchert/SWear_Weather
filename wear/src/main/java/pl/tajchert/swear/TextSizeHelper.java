package pl.tajchert.swear;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by tajchert on 17.04.15.
 */
public class TextSizeHelper {
    public static float getAutofitTextSize(CharSequence text, TextPaint paint, float targetWidth, int maxLines, float low, float high, float precision, DisplayMetrics displayMetrics) {
        float mid = (low + high) / 2.0f;
        int lineCount = 1;
        StaticLayout layout = null;

        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid,
                displayMetrics));

        if (maxLines != 1) {
            layout = new StaticLayout(text, paint, (int)targetWidth, Layout.Alignment.ALIGN_NORMAL,
                    1.0f, 0.0f, true);
            lineCount = layout.getLineCount();
        }
        if (lineCount > maxLines) {
            // For the case that `text` has more newline characters than `maxLines`.
            if ((high - low) < precision) {
                return low;
            }
            return getAutofitTextSize(text, paint, targetWidth, maxLines, low, mid, precision,
                    displayMetrics);
        }
        else if (lineCount < maxLines) {
            return getAutofitTextSize(text, paint, targetWidth, maxLines, mid, high, precision,
                    displayMetrics);
        }
        else {
            float maxLineWidth = 0;
            if (maxLines == 1) {
                maxLineWidth = paint.measureText(text, 0, text.length());
            } else {
                for (int i = 0; i < lineCount; i++) {
                    if (layout.getLineWidth(i) > maxLineWidth) {
                        maxLineWidth = layout.getLineWidth(i);
                    }
                }
            }

            if ((high - low) < precision) {
                return low;
            } else if (maxLineWidth > targetWidth) {
                return getAutofitTextSize(text, paint, targetWidth, maxLines, low, mid, precision, displayMetrics);
            } else if (maxLineWidth < targetWidth) {
                return getAutofitTextSize(text, paint, targetWidth, maxLines, mid, high, precision, displayMetrics);
            } else {
                return mid;
            }
        }
    }
}
