package com.lotterental.generalrental.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (mWebView != null) {
            if (mWebView.getUrl().contains("login/loginMain") || mWebView.getUrl().contains("common/commonMain") || !mWebView.canGoBack()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("앱을 종료 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create();
                builder.show();
            } else {
                mWebView.goBack();
            }
        }
    }
}
