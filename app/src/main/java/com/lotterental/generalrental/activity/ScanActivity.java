package com.lotterental.generalrental.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.lotterental.common.Common;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityScanBinding;
import com.lotterental.generalrental.databinding.ItemScannerCodeBinding;
import com.lotterental.generalrental.product.ChatServiceInit;
import com.lotterental.generalrental.util.LPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ScanActivity extends AppCompatActivity {
    private ActivityScanBinding mBinding = null;

    private ArrayList<String> mListViewItemList = new ArrayList<>();
    private BarcodeDataAdapter mAdapter = null;

    private String mReqNo = null;
    private String mVbeln = null;
    private String mZinout = null;

    private int mLimitCnt = 0;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan);

        if (mBinding.getRoot().getId() == R.id.normal) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        if (getIntent().getStringExtra(JavaScriptBridge.PARAM) != null) {
            try {
                JSONObject obj = new JSONObject(getIntent().getStringExtra(JavaScriptBridge.PARAM));
                mBinding.tvTitle.setText(obj.getString("TITLE"));
                mBinding.tvTotalNum.setText(obj.getString("TOTALCT"));
                mLimitCnt = Integer.parseInt(obj.getString("TOTALCT"));

                if (obj.getString("TITLE").equals("자산대수")) {
                    mReqNo = obj.getString("REQNO");
                    mVbeln = obj.getString("VBELN");
                    mZinout = obj.getString("ZINOUT");
                } else {
                    mReqNo = obj.getString("MATNR");
                    mVbeln = obj.getString("VBELN");
                    mZinout = obj.getString("KUNNR");
                }

                JSONArray list = obj.getJSONArray("LIST");
                for (int i = 0; i < list.length(); i++) {
                    mListViewItemList.add(((JSONObject) list.get(i)).getString("SERGE"));
                }

                mAdapter = new BarcodeDataAdapter(this);
                mBinding.listViewBarcode.setAdapter(mAdapter);
            } catch (JSONException e) {
                Common.printException(e);
            }
        }

        mBinding.btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBinding.btBarcodeListSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonParameter = new JSONObject();
                    ArrayList<JSONObject> list = new ArrayList<>();
                    if (mBinding.tvTitle.getText().equals("자산대수")) {
                        jsonParameter.put("VBELN", mVbeln);
                        jsonParameter.put("ZINOUT", mZinout);
                        jsonParameter.put("REQNO", mReqNo);

                        for (int i = 0; i < mListViewItemList.size(); i++) {
                            JSONObject jsonSerge = new JSONObject();
                            jsonSerge.put("VBELN", mVbeln);
                            jsonSerge.put("ZINOUT", mZinout);
                            jsonSerge.put("SERGE", mListViewItemList.get(i));
                            jsonSerge.put("REQNO", mReqNo);
                            list.add(jsonSerge);
                        }
                    } else {
                        jsonParameter.put("VBELN", mVbeln);
                        jsonParameter.put("KUNNR", mZinout);
                        jsonParameter.put("MATNR", mReqNo);

                        for (int i = 0; i < mListViewItemList.size(); i++) {
                            JSONObject jsonSerge = new JSONObject();
                            jsonSerge.put("VBELN", mVbeln);
                            jsonSerge.put("KUNNR", mZinout);
                            jsonSerge.put("SERGE", mListViewItemList.get(i));
                            jsonSerge.put("MATNR", mReqNo);
                            list.add(jsonSerge);
                        }
                    }

                    jsonParameter.put("IT_DATA", new JSONArray(list));

                    Intent i = new Intent();
                    i.putExtra(JavaScriptBridge.PARAM, jsonParameter.toString());
                    setResult(RESULT_OK, i);
                    finish();

                } catch (JSONException e) {
                    Common.printException(e);
                }

            }
        });

        LPermission.getInstance().checkCameraPermission(this, new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                startScan(savedInstanceState);
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ChatServiceInit.getInstance(getApplication()).setBarcodeCallbackListener(new ChatServiceInit.BarcodeCallbackListener() {
            @Override
            public void barcodeCallback(String barcode) {
                dataSetCheck(barcode);
            }
        });
        if (capture != null)
            capture.onResume();
    }

    private BarcodeCallback mBarcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            dataSetCheck(result.getText());
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            return;
        }
    };

    private void dataSetCheck(String barcode) {
        if (mLimitCnt > 0 && mListViewItemList.size() >= mLimitCnt) {
            return;
        }

        boolean isExist = false;

        for (String elements : mListViewItemList) {
            if (elements.equals(barcode)) {
                isExist = true;
            }
        }

        if (!isExist) {
            mListViewItemList.add(0, barcode);
            mAdapter.notifyDataSetChanged();
        }

        mBinding.tvScanNum.setText(Integer.toString(mListViewItemList.size()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (capture != null)
            capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (capture != null)
            capture.onDestroy();
    }

    private void startScan(Bundle savedInstanceState) {
        barcodeScannerView = mBinding.viewBarcodeScan;
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.initializeFromIntent(getIntent());
        barcodeScannerView.decodeContinuous(mBarcodeCallback);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class BarcodeDataAdapter extends BaseAdapter {
        private Context mContext = null;

        public BarcodeDataAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mListViewItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return mListViewItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ItemScannerCodeBinding scannerCodeBinding;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scanner_code, null);
                scannerCodeBinding = DataBindingUtil.bind(convertView);
                convertView.setTag(scannerCodeBinding);
                scannerCodeBinding.btRemove.setTag(position);
            } else {
                scannerCodeBinding = (ItemScannerCodeBinding) convertView.getTag();
                scannerCodeBinding.btRemove.setTag(position);
            }

            scannerCodeBinding.tvNum.setText(Integer.toString(position + 1));
            scannerCodeBinding.tvBarcode.setText(mListViewItemList.get(position));
            scannerCodeBinding.btRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, mListViewItemList.get((int)v.getTag()) + " 가 삭제되었습니다,", Toast.LENGTH_SHORT).show();
                    mListViewItemList.remove((int)v.getTag());
                    notifyDataSetChanged();
                }
            });
            return scannerCodeBinding.getRoot();
        }
    }
}
