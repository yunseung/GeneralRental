package com.lotterental.generalrental.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lotterental.generalrental.webview.LWebView;

/**
 * BaseActivity.
 *
 * 하나의 웹뷰를 여러 액티비티가 공유해서 사용할 수 있도록 만든 클래스. (웹뷰 내의 팝업 등..)
 * 하지만 지금은 Main에서만 사용한다.
 * 2019-02-13
 *
 * yunseung kim.
 * yun_87k@naver.com
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
