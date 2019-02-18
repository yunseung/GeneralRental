package com.lotterental.generalrental.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
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
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;
import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.common.util.CommonUtils;
import com.lotterental.generalrental.BuildConfig;
import com.lotterental.generalrental.Const;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityMainBinding;
import com.lotterental.generalrental.network.PrinterSocketAsyncTask;
import com.lotterental.generalrental.product.ChatServiceInit;
import com.lotterental.generalrental.util.LPermission;
import com.lotterental.generalrental.util.preferences.LPreferences;
import com.lotterental.generalrental.webview.JavascriptAPI;
import com.lotterental.generalrental.webview.JavascriptSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


/**
 * MainActivity.
 *
 * 웬만한 Web 과의 통신은 여기서 한다.
 *
 * Bluetooth 초기화, 루팅검사, mMoin 연동 등..
 *
 * 2019-02-13
 *
 * yunseung kim.
 * yun_87k@naver.com
 */

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding = null;
    private String mCallback = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private boolean isRooting = false;

    private JSONObject mSsoParam = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        // 루팅검사
        try {
            Runtime.getRuntime().exec("su");
            isRooting = true;
        } catch (Exception e) {
            isRooting = false;
        }

        if (!isRooting) {
            isRooting = CommonUtils.checkRootingFiles(CommonUtils.createFiles(RootFilesPath));
        }


        if (isRooting) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("불법 루팅된 단말에서는 사용할 수 없습니다.");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
        }
        // 루팅검사 끝
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        LPermission.getInstance().checkPhoneStatePermission(getApplicationContext(), new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    JSONObject ssoInfo = new JSONObject();
                    ssoInfo.put("v", getIntent().getStringExtra("v")); // appVersion
                    ssoInfo.put("u", getIntent().getStringExtra("u")); // userId
//            ssoInfo.put("u", "M00040"); // userId
                    ssoInfo.put("ci", getIntent().getStringExtra("ci")); // companyId
                    ssoInfo.put("cc", getIntent().getStringExtra("cc")); // companyCode
                    ssoInfo.put("cn", getIntent().getStringExtra("cn")); // companyName

                    mSsoParam = new JSONObject();
                    mSsoParam.put("SSO_INFO", ssoInfo);
                    mSsoParam.put("DEVICE_ID", CommonUtils.getDeviceIMEI(getApplicationContext()));
                    mSsoParam.put("FCM_TOKEN", LPreferences.getToken(getApplicationContext()));

                } catch (JSONException e) {
                    Common.printException(e);
                }
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });

        mBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        if (mBinding.getRoot().getId() == R.id.normal) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        mWebView = mBinding.mainWebView;

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        initializeElements();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isEnabled()) {
            setChatServiceListener();
        } else {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Const.BT_REQUEST_ENABLE);
        }
    }

    @Override
    protected void onDestroy() {
        ChatServiceInit.getInstance(getApplication()).disconnect();
        ChatServiceInit.getInstance(getApplication()).killInstance();
        super.onDestroy();
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

    private void setChatServiceListener() {
        ChatServiceInit.getInstance(getApplication()).setBarcodeCallbackListener(new ChatServiceInit.BarcodeCallbackListener() {
            @Override
            public void barcodeCallback(String barcode) {
                try {
                    JSONObject param = new JSONObject();
                    // barcode bluetooth 기기를 사용할 때에는 무조건 barcodeSearch 로 보낸다.
                    param.put("barcodeSearch", barcode);
                    JavascriptSender.getInstance().callJavascriptFunc(mWebView, "barcodeSearch", param);
                } catch (JSONException e) {
                    Common.printException(e);
                }
            }
        });
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

    public void onStartExcelActivity(View v) {

    }

    public void startFullScanActivity(String callback) {
        mCallback = callback;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(false);
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

    public void startPhoneCall(final JSONObject obj) {
        LPermission.getInstance().checkPhoneStatePermission(getApplicationContext(), new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + obj.getString("TEL_NUM")));
                    startActivity(intent);
                } catch (JSONException | SecurityException e) {
                    Common.printException(e);
                }
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(getApplicationContext(), "전화를 걸 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void reqAppInfo(final String callback) {
        LPermission.getInstance().checkPhoneStatePermission(getApplicationContext(), new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
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

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });
    }

    public void reqExcelDownload(final JSONObject obj, final String callback) {
        LPermission.getInstance().checkStoragePermission(this, new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
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
                            sheetA.addCell(new Label(0, i, ((JSONObject) array.get(i)).getString("INDEX")));
                            sheetA.addCell(new Label(1, i, ((JSONObject) array.get(i)).getString("EQUNR")));
                        }

                        writableWorkbook.write();
                        writableWorkbook.close();


                        Toast.makeText(getApplicationContext(), "엑셀 내보내기 완료", Toast.LENGTH_SHORT).show();

                        JSONObject param = new JSONObject();
                        param.put(callback, "succ");
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, param);
                    } catch (IOException | WriteException | JSONException e) {
                        Common.printException(e);

                        Toast.makeText(getApplicationContext(), "엑셀 내보내기 실패", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject param = new JSONObject();
                            param.put(callback, "fail");
                            JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, param);
                        } catch (JSONException e1) {
                            Common.printException(e1);
                        }
                    }
                } catch (JSONException e) {
                    Common.printException(e);
                }
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });
    }

    public void needUpdate(JSONObject param) {
        try {
            if (param.getString("IS_NECESSARY").equals("Y")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("업데이트 후 사용 가능합니다.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.APP_DOWNLOAD_URL));
                        startActivity(browserIntent);
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
            // else 는 사용 안하고 있음. (웹에서 Y만 보냄)
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("새로운 버전이 있습니다. 업데이트 하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.APP_DOWNLOAD_URL));
                        startActivity(browserIntent);
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
            }
        } catch (JSONException e) {
            Common.printException(e);
        }
    }

    public void cannotReceiveToken(String callback) {
        mCallback = callback;
        String token = FirebaseInstanceId.getInstance().getToken();

        if (token.isEmpty()) {
            mHandler.sendEmptyMessageDelayed(Const.FCM_TOKEN_REQ, 3000);
        } else {
            try {
                JSONObject param = new JSONObject();
                param.put(mCallback, token);
                JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, param);
                LPreferences.setToken(getApplicationContext(), token);
            } catch (JSONException e) {
                Common.printException(e);
            }
        }
    }

    public void onReqSsoInfo(String callback) {
        try {
            if (mSsoParam.getJSONObject("SSO_INFO").has("u")) {
                JavascriptSender.getInstance().callJavascriptFunc(mWebView, callback, mSsoParam);
            } else {
                return;
            }
        } catch (JSONException e) {
            Common.printException(e);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Const.FCM_TOKEN_REQ:
                    cannotReceiveToken(mCallback);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // if (full scan 에서 온 결과 (단건)) else (다건 스캔 결과)
                    if (data.hasExtra("FULL_SCAN_BARCODE")) {
//                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, data.getStringExtra("FULL_SCAN_BARCODE"));
                        try {
                            JSONObject param = new JSONObject();
                            param.put(mCallback, data.getStringExtra("FULL_SCAN_BARCODE"));
                            JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, param);
                        } catch (JSONException e) {
                            Common.printException(e);
                        }
                    } else {
                        JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, data.getStringExtra(JavaScriptBridge.PARAM));
                    }
                }
                break;
            case Const.REQ_SCAN_PROCESS:
                if (resultCode == RESULT_OK) {
                    JavascriptSender.getInstance().callJavascriptFunc(mWebView, mCallback, data.getStringExtra(JavaScriptBridge.PARAM));
                }
                break;

            case Const.BT_REQUEST_ENABLE:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setChatServiceListener();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    LLog.d("BT not enabled");
                    Toast.makeText(this, "단말기의 블루투스를 활성화 해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    /**
     * 루팅검사에 필요한 root
     */
    public static final String ROOT_PATH = Environment.
            getExternalStorageDirectory() + "";
    private static final String ROOTING_PATH_1 = "/system/bin/su";
    private static final String ROOTING_PATH_2 = "/system/xbin/su";
    private static final String ROOTING_PATH_3 = "/system/app/SuperUser.apk";
    private static final String ROOTING_PATH_4 = "/data/data/com.noshufou.android.su";
    private static final String ROOTING_PATH_5 = "/su/bin/su";
    private static final String ROOTING_PATH_6 = "/su/xbin/su";
    private static final String ROOTING_PATH_7 = "/su/bin/.user/.su";
    private static final String ROOTING_PATH_8 = "system/bin/.user/.su";
    private static final String ROOTING_PATH_9 = "dev/com.noshufou.android.su";
    private static final String ROOTING_PATH_10 = "data/data/com.tegrak.lagfix";
    private static final String ROOTING_PATH_11 = "data/data/eu.chainfire.supersu";
    private static final String ROOTING_PATH_12 = "data/data/com.jrummy.root.browserfree";
    private static final String ROOTING_PATH_13 = "data/app/com.tegrak.lagfix.apk";
    private static final String ROOTING_PATH_14 = "data/app/eu.chainfire.supersu.apk";
    private static final String ROOTING_PATH_15 = "data/app/com.noshufou.android.su.apk";
    private static final String ROOTING_PATH_16 = "data/app/com.jrummy.root.browserfree.apk";

    public String[] RootFilesPath = new String[]{
            ROOT_PATH + ROOTING_PATH_1,
            ROOT_PATH + ROOTING_PATH_2,
            ROOT_PATH + ROOTING_PATH_3,
            ROOT_PATH + ROOTING_PATH_4,
            ROOT_PATH + ROOTING_PATH_5,
            ROOT_PATH + ROOTING_PATH_6,
            ROOT_PATH + ROOTING_PATH_7,
            ROOT_PATH + ROOTING_PATH_8,
            ROOT_PATH + ROOTING_PATH_9,
            ROOT_PATH + ROOTING_PATH_10,
            ROOT_PATH + ROOTING_PATH_11,
            ROOT_PATH + ROOTING_PATH_12,
            ROOT_PATH + ROOTING_PATH_13,
            ROOT_PATH + ROOTING_PATH_14,
            ROOT_PATH + ROOTING_PATH_15,
            ROOT_PATH + ROOTING_PATH_16
    };
}
