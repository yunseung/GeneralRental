package com.lotterental.common;

import android.util.Log;

public class Common {

    public static void printException(Exception e) {
        StackTraceElement[] temp = e.getStackTrace();
        for (StackTraceElement ste : temp) {
            Log.w("com.lotterental.general",  ste.toString());
        }
    }

}
