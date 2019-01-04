package com.lotterental.generalrental.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
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
import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.generalrental.R;
import com.lotterental.generalrental.databinding.ActivityExcelBinding;
import com.lotterental.generalrental.databinding.ItemItemRowBinding;
import com.lotterental.generalrental.item.ExcelListItem;
import com.lotterental.generalrental.util.LPermission;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelActivity extends BaseActivity {

    private ActivityExcelBinding mBinding = null;

    private ExcelListAdapter mAdapter = null;
    private ArrayList<ExcelListItem> mExcelList = new ArrayList<>();

    private CaptureManager capture = null;
    private DecoratedBarcodeView barcodeScannerView = null;

    private String mExcelFileName = null;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(ExcelActivity.this, R.layout.activity_excel);

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


        mBinding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAdapter = new ExcelListAdapter();
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

    private void startScan(Bundle savedInstanceState) {
        barcodeScannerView = mBinding.viewBarcodeScan;
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeScannerView.initializeFromIntent(getIntent());
        barcodeScannerView.decodeContinuous(mBarcodeCallback);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
    }

    private BarcodeCallback mBarcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            // 스캔 결과가 excel list 에 있는 eqNo 와 같은게 있다면 체크한다.
            dataSetCheck(result.getText());
    }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            return;
        }
    };

    @Override
    protected void handleMassageBarcode(String barcode) {
        super.handleMassageBarcode(barcode);
        dataSetCheck(barcode);
    }

    private void dataSetCheck(String barcode) {
        // 스캔 결과가 excel list 에 있는 eqNo 와 같은게 있다면 체크한다.
        for (int i = 0; i < mExcelList.size(); i++) {
            if ((mExcelList.get(i).getEqNo()).equals(barcode)) {
                mExcelList.get(i).setIsExist("완료");
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void onOpenExplorerClick(View v) {
        // String[] mimeTypes = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"}; // .xls & .xlsx

        LPermission.getInstance().checkStoragePermission(this, new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                excelImport();
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });

    }

    public void onExportExcelClick(View v) {
        LPermission.getInstance().checkStoragePermission(this, new LPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                if (mExcelList.size() > 0) {
                    excelExport();
                } else {
                    Toast.makeText(getApplicationContext(), "내보낼 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });
    }

    private void excelImport() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        {
            intent.setType("application/vnd.ms-excel\",\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/*");
            startActivityForResult(Intent.createChooser(intent, "ChooseFile"), 11);
        }
    }

    private void excelExport() {
        File sd = Environment.getExternalStorageDirectory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
        String csvFile = mExcelFileName.substring(0, mExcelFileName.length()-4) + sdf.format(new Date()) + ".xls";

        File directory = new File(sd.getAbsolutePath());

        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        try {
            File file = new File(directory, csvFile);
            WorkbookSettings workbookSettings = new WorkbookSettings();
            workbookSettings.setLocale(new Locale(Locale.KOREAN.getLanguage(), Locale.KOREAN.getCountry()));

            WritableWorkbook writableWorkbook = Workbook.createWorkbook(file, workbookSettings);

            WritableSheet sheetA = writableWorkbook.createSheet("sheet A", 0);

            int rowCnt = mExcelList.size();

            for (int i = 0; i < rowCnt; i++) {
                sheetA.addCell(new Label(0, i, mExcelList.get(i).getEqNo()));
                sheetA.addCell(new Label(1, i, mExcelList.get(i).getModelNm()));
                sheetA.addCell(new Label(2, i, mExcelList.get(i).getSerialNo()));
                sheetA.addCell(new Label(3, i, mExcelList.get(i).getIsExist()));
            }

            writableWorkbook.write();
            writableWorkbook.close();

            Toast.makeText(getApplicationContext(), "엑셀 내보내기 완료", Toast.LENGTH_SHORT).show();
        } catch (IOException | WriteException e) {
            Common.printException(e);
            Toast.makeText(getApplicationContext(), "엑셀 내보내기 실패", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            File excelFile = new File(fileUri.toString());

            if ((fileUri.toString()).startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(fileUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        mExcelFileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if ((fileUri.toString()).startsWith("file://")) {
                mExcelFileName = excelFile.getName();
            }

            Workbook workbook = null;
            Sheet sheet = null;
            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                workbook = Workbook.getWorkbook(inputStream);
                sheet = workbook.getSheet(0);
                int maxColumn = 3;
                int rowStart = 1;
                int rowEnd = sheet.getColumn(maxColumn - 1).length - 1;
                int columnStart = 0;
                int columnEnd = sheet.getRow(2).length - 1;

                for (int row = rowStart; row <= rowEnd; row++) {
                    ExcelListItem item = new ExcelListItem();
                    item.setEqNo(sheet.getCell(0, row).getContents());
                    item.setModelNm(sheet.getCell(1, row).getContents());
                    item.setSerialNo(sheet.getCell(2, row).getContents());

                    mExcelList.add(item);
                }
            } catch (IOException | BiffException e) {
                Common.printException(e);
            } finally {
                mBinding.lvExcel.setAdapter(mAdapter);
                workbook.close();
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class ExcelListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mExcelList.size();
        }

        @Override
        public Object getItem(int position) {
            return mExcelList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ItemItemRowBinding binding;
            if (convertView == null) {
                convertView = LayoutInflater.from(ExcelActivity.this).inflate(R.layout.item_item_row, null);
                binding = DataBindingUtil.bind(convertView);
                convertView.setTag(binding);
                binding.tvCheck.setTag(position);
            } else {
                binding = (ItemItemRowBinding) convertView.getTag();
            }

            binding.machineNo.setText(mExcelList.get(position).getEqNo());
            binding.modelNm.setText(mExcelList.get(position).getModelNm());
            binding.serialNo.setText(mExcelList.get(position).getSerialNo());
            binding.tvCheck.setText(mExcelList.get(position).getIsExist());

            return binding.getRoot();
        }
    }
}
