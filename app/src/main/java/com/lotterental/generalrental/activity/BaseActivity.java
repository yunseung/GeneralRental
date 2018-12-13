package com.lotterental.generalrental.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lotterental.generalrental.webview.LWebView;

/**
 * Created by macpro on 2018. 7. 3..
 */

/**
 * BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity {
    public LWebView mWebView = null;
    public String mOCRCallback = null;
    public String mTransKeyCallbackFunc = null;
    public boolean mIsPopup = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.destroyDrawingCache();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }

        super.onBackPressed();
    }
}
