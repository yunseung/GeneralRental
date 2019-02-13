package com.lotterental.generalrental.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lotterental.LLog;

import java.lang.ref.WeakReference;

/**
 * GeneralRentalWebClient.
 *
 * WebView 의 상태를 모니터링 및 관리해줄 수 있는 클래스.
 * native - webView 통신간 timing issue 가 발생할 때 이 클래스를 활용할 수 있다.
 *
 * 2019-02-13
 *
 * yunseung kim.
 * yun_87k@naver.com
 */

public final class GeneralRentalWebClient extends WebViewClient {
    private final String TAG = GeneralRentalWebClient.class.getSimpleName();

    protected WeakReference<Activity> activityRef;


    public GeneralRentalWebClient(Activity activity) {
        this.activityRef = new WeakReference<Activity>(activity);
    }

    // https, ssl 등의 보안 오류가 발생했음을 앱에 알려주는 함수.
    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        handler.proceed();
    }

    // page loading 끝나고 호출되는 함수. clearHistory() 등 몇 함수는 웹뷰 로딩이 끝난 후 이곳에서 해야 먹히기 때문에 구현해둠.
    @Override
    public void onPageFinished(WebView view, String url) {
        //구동후 빈 웹뷰 화면 안보여주기 위해
        if (view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        super.onPageFinished(view, url);
    }


    // 웹뷰의 상태 또는 페이지가 바뀔 때 마다 호출되는 곳.
    // javascript 통신처럼 여기서 url parameter 를 통한 원활한 통신 수신 가능.
    @SuppressWarnings("deprecation")
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.e("url ", url);
        if(url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".png")) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.allowScanningByMediaScanner();

            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            //마지막 구분자를 파일명으로 지정. 확장자를 포함하여야 내 파일에서 열린다.
            String filename[] = url.split("/");
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,    //Download folder
                    filename[filename.length-1]);       //Name of file

            DownloadManager dm = null;
            try {
                dm = (DownloadManager) activityRef.get().getSystemService(activityRef.get().DOWNLOAD_SERVICE);
            } catch (RuntimeException e) {
                LLog.printException(e);
            }


            dm.enqueue(request);

        } else {
            view.loadUrl(url);
        }
        return true;
    }

    // 웹뷰의 상태 또는 페이지가 바뀔 때 마다 호출되는 곳.
    // javascript 통신처럼 여기서 url parameter 를 통한 원활한 통신 수신 가능.
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return true;
    }
    // 웹뷰에서 발생할 수 있는 에러들이 들어온다. 그때그때 처리하면 됨.
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(BuildConfig.IS_DEV  || BuildConfig.IS_TEST){ //개발 모드일때 확인
                CharSequence des = error.getDescription();
                LLog.e("++ onReceivedError : " + des.toString());
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setTitle(mContext.getResources().getString(R.string.alert));
//
//                builder.setMessage("[wifi 설정 개발 테스트 확인요망] "+ "\n" +error.getDescription());
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
        }
    }

    // HTTP Error
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        LLog.d(">> onReceivedHttpError");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if(BuildConfig.IS_DEV || BuildConfig.IS_TEST) { //개발 모드일때 확인
                Uri url = request.getUrl();
                String text = url.toString();
                try {
                    Toast.makeText(activityRef.get(), text + " 리소스를 찾을수 없습니다.", Toast.LENGTH_SHORT).show();
                } catch (RuntimeException e) {
                    LLog.printException(e);
                }


//            }
        }
    }
}
