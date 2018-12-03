package com.lotterental.generalrental.util;

import android.content.Context;

import com.lotterental.LLog;
import com.lotterental.generalrental.util.preferences.LPreferences;


/**
 * UUID 생성에 필요한 class.
 */
public class LUUID {
    private static String uniqueID = null;
    /**
     * 랜덤 UUID를 생성한다.
     * @param context
     * @return
     */
    public static synchronized String getUUID(Context context) {
        if(uniqueID != null){
            LLog.d("++ uniqueID : "+uniqueID);
            return uniqueID;
        }
        uniqueID = LPreferences.getUUID(context);
        if(uniqueID == null){
            uniqueID = java.util.UUID.randomUUID().toString();
            LPreferences.setUUID(context, uniqueID);
        }
        LLog.d("++ uniqueID : "+uniqueID);
        return uniqueID;
    }
}
