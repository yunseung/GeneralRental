package com.lotterental.generalrental.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.Result;
import com.lotterental.common.Common;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityScanBinding;
import com.lotterental.generalrental.databinding.ItemScannerCodeBinding;
import com.lotterental.generalrental.item.BarcodeListItem;
import com.lotterental.generalrental.webview.JavascriptSender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends BaseActivity {
    private ActivityScanBinding mBinding = null;

    private ZXingScannerView mZXingScannerView = null;

    private ArrayList<BarcodeListItem> mListViewItemList = new ArrayList<BarcodeListItem>();
    private BarcodeDataAdapter mAdapter = null;

    private String mCallback = null;
    private String mReqNo = null;
    private String mVbeln = null;
    private String mZinout = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan);

        try {
            JSONObject obj = new JSONObject(getIntent().getStringExtra(JavaScriptBridge.PARAM));
            mCallback = getIntent().getStringExtra(JavaScriptBridge.CALLBACK);

            mBinding.tvTotal.setText(obj.getString("TITLE"));
            mBinding.tvTotalNum.setText(obj.getString("TOTALCT"));
            mReqNo = obj.getString("REQNO");
            mVbeln = obj.getString("VBELN");
            mZinout = obj.getString("ZINOUT");

            JSONArray list = obj.getJSONArray("LIST");
            for (int i = 0; i < list.length(); i++) {
                mListViewItemList.add(new BarcodeListItem(Integer.toString(i + 1), ((JSONObject)list.get(i)).getString("SERGE")));
            }

            Collections.reverse(mListViewItemList);

            mAdapter = new BarcodeDataAdapter(this);
            mBinding.listViewBarcode.setAdapter(mAdapter);
        } catch (JSONException e) {
            Common.printException(e);
        }

        mBinding.btBarcodeListSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonParameter = new JSONObject();

                    jsonParameter.put("VBELN",mVbeln);
                    jsonParameter.put("ZINOUT",mZinout);
                    jsonParameter.put("REQNO", mReqNo);

                    ArrayList<JSONObject> list = new ArrayList<>();

                    for(int i = 0 ; i  <  mListViewItemList.size() ; i++ ){
                        JSONObject jsonSerge = new JSONObject();
                        jsonSerge.put("VBELN", mVbeln);
                        jsonSerge.put("ZINOUT",mZinout);
                        jsonSerge.put("SERGE", mListViewItemList.get(i).getBarcode());
                        jsonSerge.put("REQNO", mReqNo);
                        list.add(jsonSerge);
                    }

                    jsonParameter.put("IT_DATA", new JSONArray(list));

                    Intent i = new Intent();
                    i.putExtra(JavaScriptBridge.PARAM, jsonParameter.toString());
                    i.putExtra(JavaScriptBridge.CALLBACK, mCallback);
                    setResult(RESULT_OK, i);
                    finish();

                } catch (JSONException e) {
                    Common.printException(e);
                }

            }
        });


        startScan();
    }

    private void startScan() {
        mZXingScannerView = new ZXingScannerView(getApplicationContext());
        mBinding.viewBarcodeScan.addView(mZXingScannerView);
        mZXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                mZXingScannerView.resumeCameraPreview(this);
                boolean isExist = false;

                for (BarcodeListItem elements : mListViewItemList) {
                    if (elements.getBarcode().equals(result.getText())) {
                        isExist = true;
                    }
                }

                if (!isExist) {
                    mListViewItemList.add(0, new BarcodeListItem(Integer.toString(mListViewItemList.size() + 1), result.getText()));
                    mAdapter.notifyDataSetChanged();
                }


            }
        });
        mZXingScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mZXingScannerView.stopCamera();
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
            } else {
                scannerCodeBinding = (ItemScannerCodeBinding) convertView.getTag();
            }

            scannerCodeBinding.tvNum.setText(mListViewItemList.get(position).getNum());
            scannerCodeBinding.tvBarcode.setText(mListViewItemList.get(position).getBarcode());
            return scannerCodeBinding.getRoot();
        }
    }
}
