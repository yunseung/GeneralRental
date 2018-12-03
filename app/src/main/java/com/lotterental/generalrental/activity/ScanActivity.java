package com.lotterental.generalrental.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.Result;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityScanBinding;
import com.lotterental.generalrental.item.BarcodeListItem;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanActivity extends BaseActivity {
    private ActivityScanBinding mBinding = null;

    private ZXingScannerView mZXingScannerView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan);
        mBinding.listViewBarcode.setAdapter(new BarcodeDataAdapter());

        startScan();
    }

    private void startScan() {
        mZXingScannerView = new ZXingScannerView(getApplicationContext());
        mBinding.viewBarcodeScan.addView(mZXingScannerView);
        mZXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_LONG).show();
                mZXingScannerView.resumeCameraPreview(this);
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

        private ArrayList<BarcodeListItem> listViewItemList = new ArrayList<BarcodeListItem>() ;

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
