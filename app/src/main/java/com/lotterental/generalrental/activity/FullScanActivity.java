package com.lotterental.generalrental.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityFullScanBinding;
import com.lotterental.generalrental.product.ChatServiceInit;
import com.lotterental.generalrental.util.LPermission;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * FullScanActivity.
 *
 * 바코드 스캔 화면. (전체화면)
 *
 * zxing library 와 Bluetooth barcode 기기만 사용하는 클래스
 *
 * 2019-02-13
 *
 * yunseung kim.
 * yun_87k@naver.com
 */
public class FullScanActivity extends AppCompatActivity {
    private ActivityFullScanBinding mBinding = null;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_scan);

        if (mBinding.getRoot().getId() == R.id.normal) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }


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
                Intent i = new Intent();
                i.putExtra("FULL_SCAN_BARCODE", barcode);
                setResult(RESULT_OK, i);
                finish();
            }
        });

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

    private BarcodeCallback mBarcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            Intent i = new Intent();
            i.putExtra("FULL_SCAN_BARCODE", result.getText());
            setResult(RESULT_OK, i);
            finish();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            return;
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    private void startScan(Bundle savedInstanceState) {
        barcodeScannerView = mBinding.viewBarcodeScan;
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.initializeFromIntent(getIntent());
        barcodeScannerView.decodeSingle(mBarcodeCallback);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
    }

}
