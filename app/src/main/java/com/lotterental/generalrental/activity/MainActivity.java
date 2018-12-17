package com.lotterental.generalrental.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.BuildConfig;
import com.lotterental.generalrental.Const;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityMainBinding;
import com.lotterental.generalrental.webview.JavascriptAPI;
import com.lotterental.generalrental.webview.JavascriptSender;
import com.lotterental.generalrental.webview.LWebView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        mWebView = mBinding.mainWebView;

        initializeElements();
    }

    /**
     * activity elements initialize.
     */
    private void initializeElements() {
        // webView initialized 할 때 마다 캐시와 히스토리 지움.
        mWebView.addJavascriptInterface(new JavaScriptBridge(MainActivity.this, mWebView, JavascriptAPI.class), JavaScriptBridge.CALL_NAME);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.loadUrl(BuildConfig.WEB_URL);
    }

    public void startScanActivity(JSONObject obj, String callback) {
        Intent i = new Intent(MainActivity.this, ScanActivity.class);
        i.putExtra(JavaScriptBridge.PARAM, obj.toString());
        i.putExtra(JavaScriptBridge.CALLBACK, callback);
        startActivityForResult(i, Const.REQ_SCAN_PROCESS);
    }

    public void startFullScanActivity(String callback) {
        Intent i = new Intent(MainActivity.this, FullScanActivity.class);
        i.putExtra(JavaScriptBridge.CALLBACK, callback);
        startActivityForResult(i, Const.REQ_FULL_SCAN_PROCESS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Const.REQ_SCAN_PROCESS:
                if (resultCode == RESULT_OK) {
                    LLog.e(data.getStringExtra(JavaScriptBridge.CALLBACK));
                    LLog.e(data.getStringExtra(JavaScriptBridge.PARAM));
                    JavascriptSender.getInstance().callJavascriptFunc(mWebView, data.getStringExtra(JavaScriptBridge.CALLBACK), data.getStringExtra(JavaScriptBridge.PARAM));
                }
                break;

            case Const.REQ_FULL_SCAN_PROCESS:
                if (resultCode == RESULT_OK) {
                    LLog.e(data.getStringExtra(JavaScriptBridge.CALLBACK));
                    LLog.e(data.getStringExtra(JavaScriptBridge.PARAM));
                    JavascriptSender.getInstance().callJavascriptFunc(mWebView, data.getStringExtra(JavaScriptBridge.CALLBACK), data.getStringExtra(JavaScriptBridge.PARAM));
                }
                break;

                default:
                    break;
        }
    }

    public void onScanClick(View v) {
        startActivity(new Intent(MainActivity.this, ScanActivity.class));
    }

    public void onExcelClick(View v) {
        startActivity(new Intent(MainActivity.this, ExcelActivity.class));
    }

    public void onPrintClick(View v) {
        startActivity(new Intent(MainActivity.this, TestActivity.class));
    }
}
