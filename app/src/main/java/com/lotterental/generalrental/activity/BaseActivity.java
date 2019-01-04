package com.lotterental.generalrental.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.PointMobile.PMSyncService.BluetoothChatService;
import com.PointMobile.PMSyncService.SendCommand;
import com.lotterental.LLog;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.Const;
import com.lotterental.generalrental.webview.JavascriptSender;
import com.lotterental.generalrental.webview.LWebView;

import java.util.ArrayList;
import java.util.List;

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

    // Local Bluetooth adapter
    public BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    public BluetoothChatService mChatService = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        LLog.e("+++ mChatService.getState(1111111111111) :  " + mChatService.getState());
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            LLog.e("onStart 1111 ++++");
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                LLog.e("onStart 2222 ++++");
                setupChat();
        }
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

        mChatService.stop();
    }

    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        SendCommand.SendCommandInit(mChatService, mHandler);

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            LLog.e("+++ mChatService.getState(1111111111111) :  " + mChatService.getState());
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
                LLog.e("???????????????? 여기뭐여 씨발 ");
                LLog.e("+++ mChatService.getState(22222222222222) :  " + mChatService.getState());
                selectDevice();
            }
        }
    }

    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // Bluetooth Receive Handling
                case Const.MESSAGE_BARCODE:
                    byte[] BarcodeBuff = (byte[]) msg.obj;
                    String barcode = new String(BarcodeBuff, 0, msg.arg1);
                    handleMassageBarcode(barcode);
                    break;
            }
        }
    };

    protected void handleMassageBarcode(String barcode) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Const.BT_REQUEST_ENABLE:
                setupChat();
                break;

            default:
                break;
        }
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

    @Override
    public void onBackPressed() {
        if (mWebView != null) {
            if (mWebView.getUrl().contains("login/loginMain") || mWebView.getUrl().contains("login/loginMain") || !mWebView.canGoBack()) {
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
