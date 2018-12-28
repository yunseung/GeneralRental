package com.lotterental.generalrental.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.view.View;

import com.PointMobile.PMSyncService.BluetoothChatService;
import com.PointMobile.PMSyncService.SendCommand;
import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.BuildConfig;
import com.lotterental.generalrental.Const;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityMainBinding;
import com.lotterental.generalrental.network.PrinterSocketAsyncTask;
import com.lotterental.generalrental.util.preferences.LPreferences;
import com.lotterental.generalrental.webview.JavascriptAPI;
import com.lotterental.generalrental.webview.JavascriptSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBinding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        if (mBinding.getRoot().getId() == R.id.normal) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        mWebView = mBinding.mainWebView;

        initializeElements();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();

                selectDevice();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            LLog.e("+++ mBluetoothAdapter.isEnabled +++");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Const.BT_REQUEST_ENABLE);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatService.stop();
    }

    private void selectDevice() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("장치 선택");

        List<String> listItems = new ArrayList<>();
        // TODO bondedDevice 가져오는 순서가.. 최근 등록된 애부터 가져오는게 아니라 이름 순으로 가져오는 느낌임.. 테스트 필요..
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            // bluetooth type check
            if (device.fetchUuidsWithSdp()) {
                ParcelUuid[] uu = device.getUuids();
                for (ParcelUuid u : uu) {
                    if (u.getUuid().toString().toUpperCase().equals("00001101-0000-1000-8000-00805F9B34FB")) {
                        listItems.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            }
        }

        if (listItems.size() > 0) {
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(items[0].toString().substring(items[0].length() - 17));
            mChatService.connect(device);
        }

    }

    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        SendCommand.SendCommandInit(mChatService, mHandler);
    }


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // Bluetooth Receive Handling
                case Const.MESSAGE_BARCODE:
                    byte[] BarcodeBuff = (byte[]) msg.obj;
                    String barcode = new String(BarcodeBuff, 0, msg.arg1);
                    JavascriptSender.getInstance().callJavascriptFunc(mWebView, "barcodeSearch", barcode);
                    break;
            }
        }
    };

    /**
     * activity elements initialize.
     */
    private void initializeElements() {
        // webView initialized 할 때 마다 캐시와 히스토리 지움.
        mWebView.addJavascriptInterface(new JavaScriptBridge(MainActivity.this, mWebView, JavascriptAPI.class), JavaScriptBridge.CALL_NAME);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.loadUrl(BuildConfig.WEB_URL);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void startScanActivity(JSONObject obj, String callback) {
        Intent i = new Intent(MainActivity.this, ScanActivity.class);
        i.putExtra(JavaScriptBridge.PARAM, obj.toString());
        i.putExtra(JavaScriptBridge.CALLBACK, callback);
        startActivityForResult(i, Const.REQ_SCAN_PROCESS);
    }

    public void startExcelActivity() {
        startActivity(new Intent(MainActivity.this, ExcelActivity.class));
    }

    public void startFullScanActivity(String callback) {
        Intent i = new Intent(MainActivity.this, FullScanActivity.class);
        i.putExtra(JavaScriptBridge.CALLBACK, callback);
        startActivityForResult(i, Const.REQ_FULL_SCAN_PROCESS);
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
            intent.setData(Uri.parse("tel:"+ obj.getString("phone_no")));
            startActivity(intent);
        } catch (JSONException | SecurityException e) {
            Common.printException(e);
        }

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

            case Const.BT_REQUEST_ENABLE:
                setupChat();
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
//        startActivity(new Intent(MainActivity.this, ScanActivity.class));
        LLog.e("TOKEN IS : " + LPreferences.getToken(this));
    }
}
