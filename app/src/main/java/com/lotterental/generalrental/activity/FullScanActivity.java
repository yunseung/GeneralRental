package com.lotterental.generalrental.activity;

import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.lotterental.common.jsbridge.JavaScriptBridge;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityFullScanBinding;
import com.lotterental.generalrental.util.LPermission;

import java.util.Arrays;
import java.util.Collection;


public class FullScanActivity extends AppCompatActivity {
    private ActivityFullScanBinding mBinding = null;
//    private ZXingScannerView mZXingScannerView = null;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    private String mCallback = null;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_scan);

        if (mBinding.getRoot().getId() == R.id.normal) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        mCallback = getIntent().getStringExtra(JavaScriptBridge.CALLBACK);

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
        if (capture != null)
            capture.onResume();
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

    @Override
    public void onBackPressed() {
        finish();
    }

    private void startScan(Bundle savedInstanceState) {
        barcodeScannerView = mBinding.viewBarcodeScan;
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.initializeFromIntent(getIntent());
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }
}
