package com.lotterental.generalrental.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.common.util.CommonUtils;
import com.lotterental.generalrental.BuildConfig;
import com.lotterental.generalrental.Const;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityMainBinding;
import com.lotterental.generalrental.network.PrinterSocketAsyncTask;
import com.lotterental.generalrental.util.LPermission;
import com.lotterental.generalrental.util.preferences.LPreferences;
import com.lotterental.generalrental.webview.JavascriptAPI;
import com.lotterental.generalrental.webview.JavascriptSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding = null;
    private String mCallback = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        if (mBinding.getRoot().getId() == R.id.normal) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        mWebView = mBinding.mainWebView;

        LPermission.getInstance().checkPhoneStatePermission(getApplicationContext(), new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });

        initializeElements();
    }

    @Override
    protected void handleMassageBarcode(String barcode) {
        super.handleMassageBarcode(barcode);
        JavascriptSender.getInstance().callJavascriptFunc(mWebView, "barcodeSearch", barcode);
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
        mCallback = callback;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setOrientationLocked(false);
        Intent i = integrator.createScanIntent();
        i.putExtra(JavaScriptBridge.PARAM, obj.toString());
        startActivityForResult(i, IntentIntegrator.REQUEST_CODE);
    }

    public void startExcelActivity() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ExcelActivity.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    public void startFullScanActivity(String callback) {
        mCallback = callback;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(FullScanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    public void startPrintSocket(JSONObject obj, String callback) {
        PrinterSocketAsyncTask task = new PrinterSocketAsyncTask(mWebView, callback);
        if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, obj);
        } else {
            task.execute(obj);
        }
    }

    public void startPhoneCall(JSONObject obj) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + obj.getString("TEL_NUM")));
            startActivity(intent);
        } catch (JSONException | SecurityException e) {
            Common.printException(e);
        }

    }

    public void reqAppInfo(String callback) {
        JSONObject jsonParam = new JSONObject();
        try {
            jsonParam.put("DEVICE_ID", CommonUtils.getDeviceIMEI(getApplicationContext()));
            jsonParam.put("FCM_TOKEN", LPreferences.getToken(getApplicationContext()));
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            jsonParam.put("APP_VERSION", pInfo.versionName);
        } catch (PackageManager.NameNotFoundException | JSONException e) {
            Common.printException(e);
        }

        JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, jsonParam);
    }

    public void reqExcelDownload(JSONObject obj, String callback) {
        try {
            JSONArray array = obj.getJSONArray("LIST");
            String fileName = obj.getString("FILE_NM");

            File sd = Environment.getExternalStorageDirectory();

            File directory = new File(sd.getAbsolutePath());

            if (!directory.isDirectory()) {
                directory.mkdirs();
            }

            try {
                File file = new File(directory, fileName);
                WorkbookSettings workbookSettings = new WorkbookSettings();
                workbookSettings.setLocale(new Locale(Locale.KOREAN.getLanguage(), Locale.KOREAN.getCountry()));

                WritableWorkbook writableWorkbook = Workbook.createWorkbook(file, workbookSettings);

                WritableSheet sheetA = writableWorkbook.createSheet("sheet A", 0);

                int rowCnt = array.length();

                for (int i = 0; i < rowCnt; i++) {
                    sheetA.addCell(new Label(0, i, ((JSONObject)array.get(i)).getString("INDEX")));
                    sheetA.addCell(new Label(1, i, ((JSONObject)array.get(i)).getString("EQUNR")));
                }

                writableWorkbook.write();
                writableWorkbook.close();

                Toast.makeText(getApplicationContext(), "엑셀 내보내기 완료", Toast.LENGTH_SHORT).show();
                JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, "succ");
            } catch (IOException | WriteException e) {
                Common.printException(e);
                Toast.makeText(getApplicationContext(), "엑셀 내보내기 실패", Toast.LENGTH_SHORT).show();
                JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, "fail");
            }
        } catch (JSONException e) {
            Common.printException(e);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (data.hasExtra("READER_BARCODE")) {
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, data.getStringExtra("READER_BARCODE"));
                    } else {
                        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                        String re = scanResult.getContents();
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, re);
                    }
                }
                break;
            case Const.REQ_SCAN_PROCESS:
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
//        startActivity(new Intent(MainActivity.this, ScanActivity.class));
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    public void onExcelClick(View v) {
        startActivity(new Intent(MainActivity.this, ExcelActivity.class));
    }

    public void onPrintClick(View v) {
//        startActivity(new Intent(MainActivity.this, ScanActivity.class));
        LLog.e("TOKEN IS : " + LPreferences.getToken(this));
    }
}
