package com.lotterental.generalrental.webview;

import android.content.Context;
import android.webkit.WebView;

import com.lotterental.common.jsbridge.JSBridge;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JavascriptAPI.
 *
 * webView -> native 호출 함수 모아놓은 곳.
 * @JSApi 참고.
 *
 * 2019-02-13
 *
 * yunseung kim.
 * yun_87k@naver.com
 */
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

    @JSApi(invokeMethod = "onStartExcelActivity", explain = "바코드 스캔 화면 시작", param = {""})
    public void onStartExcelActivity(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof MainActivity) {
            ((MainActivity) context).startExcelActivity();
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
            ((MainActivity) context).startPrintSocket(json.getJSONObject(JavaScriptBridge.PARAM), json.getString(JavaScriptBridge.CALLBACK));
        }
    }

    @JSApi(invokeMethod = "onStartPhoneCall", explain = "웹뷰에서 전달받은 전화번호로 전화걸기.", param = {""})
    public void onStartPhoneCall(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof  MainActivity) {
            ((MainActivity) context).startPhoneCall(json.getJSONObject(JavaScriptBridge.PARAM));
        }
    }

    @JSApi(invokeMethod = "reqAppInfo", explain = "device unique id, fcm token, app version 전달.", param = {""})
    public void reqAppInfo(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof  MainActivity) {
            ((MainActivity) context).reqAppInfo(json.getString(JavaScriptBridge.CALLBACK));
        }
    }

    @JSApi(invokeMethod = "reqExcelDownload", explain = "엑셀 다운로드.", param = {""})
    public void reqExcelDownload(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof  MainActivity) {
            ((MainActivity) context).reqExcelDownload(json.getJSONObject(JavaScriptBridge.PARAM), json.getString(JavaScriptBridge.CALLBACK));
        }
    }

    @JSApi(invokeMethod = "onNeedUpdate", explain = "버전 정보 비교하여 업데이트 필요 여부 받음..", param = {""})
    public void needUpdate(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof  MainActivity) {
            ((MainActivity) context).needUpdate(json.getJSONObject(JavaScriptBridge.PARAM));
        }
    }

    @JSApi(invokeMethod = "cannotReceiveToken", explain = "FCM TOKEN 을 받지 못햇을 경우.", param = {""})
    public void cannotReceiveToken(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof  MainActivity) {
            ((MainActivity) context).cannotReceiveToken(json.getString(JavaScriptBridge.CALLBACK));
        }
    }

    @JSApi(invokeMethod = "onReqSsoInfo", explain = "webLoading 이 완료됐다는 알림.", param = {""})
    public void onReqSsoInfo(WebView webView, Context context, JSONObject json) throws JSONException {
        if (context instanceof  MainActivity) {
            ((MainActivity) context).onReqSsoInfo(json.getString(JavaScriptBridge.CALLBACK));
        }
    }
}
