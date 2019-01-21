package com.lotterental.generalrental.product;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;

import com.PointMobile.PMSyncService.BluetoothChatService;
import com.PointMobile.PMSyncService.SendCommand;
import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.generalrental.Const;

import java.util.ArrayList;
import java.util.List;

public class ChatServiceInit {
    private static ChatServiceInit _instance = null;

    private static BluetoothChatService mChatService = null;
    private static BarcodeCallbackListener mBarcodeCallbackListener = null;
    private static Context mContext = null;
    private static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String SPP_UNIQUE_KEY = "00001101-0000-1000-8000-00805F9B34FB";

    private static final int START_SETUP_CHAT = 555;

    public interface BarcodeCallbackListener{
        void barcodeCallback(String barcode);
}

    private ChatServiceInit() {
        mHandler.sendEmptyMessageDelayed(START_SETUP_CHAT, 3000);
    }

    public static ChatServiceInit getInstance(Context ctx) {
        mContext = ctx;
        if (_instance == null) {
            _instance = new ChatServiceInit();
        }
        return _instance;
    }

    public void killInstance() {
        _instance = null;
    }

    private static class LazyHolder {
        private static ChatServiceInit INSTANCE = new ChatServiceInit();
    }

    public void setBarcodeCallbackListener(BarcodeCallbackListener listener) {
        if (mBarcodeCallbackListener != null) {
            mBarcodeCallbackListener = null;
        }
        mBarcodeCallbackListener = listener;
    }

    public void disconnect() {
        mChatService.stop();
    }

    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(mContext, mHandler);
        SendCommand.SendCommandInit(mChatService, mHandler);

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
                selectDevice();
            }
        }
    }

    private void selectDevice() {
        List<String> listItems = new ArrayList<>();
        // TODO bondedDevice 가져오는 순서가.. 최근 등록된 애부터 가져오는게 아니라 이름 순으로 가져오는 느낌임.. 테스트 필요..
        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            // bluetooth type check
            if (device.fetchUuidsWithSdp()) {
                ParcelUuid[] uu = device.getUuids();
                for (ParcelUuid u : uu) {
                    if (u.getUuid().toString().toUpperCase().equals(SPP_UNIQUE_KEY)) {
                        listItems.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            }
        }

        if (listItems.size() > 0) {
            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(items[0].toString().substring(items[0].length() - 17));
            try {
                mChatService.connect(device);
            } catch (Exception e) {
                Common.printException(e);
            }

        }

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // Bluetooth Receive Handling
                case Const.MESSAGE_BARCODE:
                    byte[] BarcodeBuff = (byte[]) msg.obj;
                    String barcode = new String(BarcodeBuff, 0, msg.arg1);
//                    mListenerList.get(mListenerList.size() - 1).barcodeCallback(barcode);
                    mBarcodeCallbackListener.barcodeCallback(barcode);
                    break;

                case START_SETUP_CHAT:
                    setupChat();
                    break;
            }
        }
    };
}
