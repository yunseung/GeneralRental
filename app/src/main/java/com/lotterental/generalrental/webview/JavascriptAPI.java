package com.lotterental.generalrental.webview;

import android.content.Context;
import android.webkit.WebView;

import com.lotterental.common.jsbridge.JSBridge;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.activity.MainActivity;

import org.apache.log4j.chainsaw.Main;
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

    @JSApi(invokeMethod = "onStartScanActivity", explain = "바코드 스캔 화면 시작", param = {""})
    public void onStartSacnActivity(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof MainActivity) {
            ((MainActivity) context).startScanActivity(json.getJSONObject(JavaScriptBridge.PARAM), json.getString(JavaScriptBridge.CALLBACK));
        }
    }

    @JSApi(invokeMethod = "onStartFullScanActivity", explain = "바코드 스캔 화면 시작", param = {""})
    public void onStartFullScanActivity(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof MainActivity) {
            ((MainActivity) context).startFullScanActivity(json.getString(JavaScriptBridge.CALLBACK));
        }
    }

    @JSApi(invokeMethod = "onStartPrintSocket", explain = "웹뷰에서 받은 바코드 데이터를 프린터 서버로 소켓 전송", param = {""})
    public void onStartPrintScoket(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof  MainActivity) {
            ((MainActivity) context).onStartPrintSocket(json.getJSONObject(JavaScriptBridge.PARAM), json.getString(JavaScriptBridge.CALLBACK));
        }
    }
}
