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
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityMainBinding;
import com.lotterental.generalrental.webview.JavascriptAPI;
import com.lotterental.generalrental.webview.LWebView;

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

//        setContentView(R.layout.activity_main);
//        mWebView = (LWebView)findViewById(R.id.main_web_view);

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
//        mWebView.loadUrl("10.106.13.148:8081/oam/mobile/as/asMain");
        mWebView.loadUrl("https://www.naver.com");
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
