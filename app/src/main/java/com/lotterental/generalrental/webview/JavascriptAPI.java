package com.lotterental.generalrental.webview;

import android.content.Context;
import android.webkit.WebView;

import com.lotterental.common.jsbridge.JSBridge;

import org.json.JSONException;
import org.json.JSONObject;

public class JavascriptAPI extends JSBridge {

    /**
     * 웹뷰 히스토리 클리어
     *
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "historyClear", explain = "히스토리 클리어", param = {""})
    public void historyClear(WebView webview, Context context, JSONObject json) throws JSONException {
        webview.clearHistory();
    }

    /**
     * 웹뷰 뒤로가기 </br>
     * 웹뷰에서 하드웨어 백버튼을 제어하기 위해서</br>
     * 하드웨어 백버튼 클릭시 네이티브는 웹뷰의 onAndroidHardwareBackPressed를 호출 하고 </br>
     * 웹뷰 com.util.js 에서 @JSApi(invokeMethod = "onBackPressed") 를 호출하여 webview.goBack(); 을 호출하게 설계됨</br>
     * @param webview
     * @param context
     * @param json
     * @throws JSONException
     */
    @JSApi(invokeMethod = "onBackPressed", explain = "웹뷰 백", param = {""})
    public void onBackPressed(WebView webview, Context context, JSONObject json) throws JSONException {
        if (webview.canGoBack()) {
            webview.goBack();
        }
    }
}
