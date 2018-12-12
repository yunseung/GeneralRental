package com.lotterental.generalrental.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.Result;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.Const;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityFullScanBinding;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class FullScanActivity extends AppCompatActivity {
    private ActivityFullScanBinding mBinding = null;

    private String mCallback = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_scan);

        mCallback = getIntent().getStringExtra(JavaScriptBridge.CALLBACK);

        startScan();
    }

    private void startScan() {
        final ZXingScannerView zXingScannerView = new ZXingScannerView(getApplicationContext());
        mBinding.viewBarcodeScan.addView(zXingScannerView);
        zXingScannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                zXingScannerView.resumeCameraPreview(this);

                Intent i = new Intent();
                i.putExtra(JavaScriptBridge.PARAM, result.getText());
                i.putExtra(JavaScriptBridge.CALLBACK, mCallback);
                setResult(RESULT_OK, i);
                finish();
            }
        });
        zXingScannerView.startCamera();
    }
}
