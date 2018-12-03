package com.lotterental.common.util;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by ComAg on 16. 3. 16..
 */
public class DisplayUtil {

    /* Device 해상도를 가지고 온다(px) */
    public static int[] getDisplaySize(Context context) {

        Display display = ((AppCompatActivity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics realMetrics = new DisplayMetrics();
        display.getRealMetrics(realMetrics);

        return new int[]{realMetrics.widthPixels, realMetrics.heightPixels};
    }

    /**
     * 키보드를 보여준다.
     *
     * @param editText
     * @param context
     */
    public static void showKeyboard(EditText editText, Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 키보드를 숨긴다.
     *
     * @param editText
     * @param context
     */
    public static void hideKeyboard(EditText editText, Context context) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
