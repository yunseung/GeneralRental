package com.lotterental.generalrental.util;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.lotterental.generalrental.Const;
import com.lotterental.generalrental.R;

import java.util.ArrayList;

/**
 * Android 6.0 이상에 대응한 request permission class.
 */
public class LPermission {

    private static class LazyHolder {
        private static final LPermission instance = new LPermission();
    }

    public static LPermission getInstance() {
        return LazyHolder.instance;
    }

    public interface PermissionGrantedListener {
        void onPermissionGranted();

        void onPermissionDenied();
    }

    /**
     * 앱 필수 구동 조건을 확인한다.
     *
     * @param context
     */
    public void checkNecessaryPermission(final Context context, final PermissionGrantedListener permissionGrantedListener) {
        TedPermission.with(context)
                .setGotoSettingButtonText(R.string.setting)
                .setDeniedMessage(R.string.permission_ncessary_setting)
                .setPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.GET_ACCOUNTS)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        //권한 설정 안할시 앱 종료
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.alert);
                        builder.setMessage(R.string.permission_ncessary_check);
                        builder.setPositiveButton(R.string.confirm,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((AppCompatActivity) context).setResult(Const.RES_APP_FINSISH, new Intent());
                                        ((AppCompatActivity) context).finish();
                                    }
                                });
                        builder.show();
                    }
                }).check();
    }

    /**
     * 카메라 퍼미션
     *
     * @param context
     * @param permissionGrantedListener
     */
    public void checkCameraPermission(final Context context, final PermissionGrantedListener permissionGrantedListener) {
        TedPermission.with(context)
                .setGotoSettingButtonText(R.string.setting)
                .setDeniedMessage(R.string.permission_ncessary_setting)
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionDenied();
                        }
                    }

                }).check();
    }

    /**
     * 지문인식 퍼미션
     *
     * @param context
     * @param permissionGrantedListener
     */
    public void checkFidoPermission(final Context context, final PermissionGrantedListener permissionGrantedListener) {
        TedPermission.with(context)
                .setGotoSettingButtonText(R.string.setting)
                .setDeniedMessage(R.string.permission_ncessary_setting)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionDenied();
                        }
                    }

                }).check();
    }

    /**
     * 파일 접근 퍼미션
     *
     * @param context
     * @param permissionGrantedListener
     */
    public void checkStoragePermission(final Context context, final PermissionGrantedListener permissionGrantedListener) {
        TedPermission.with(context)
                .setGotoSettingButtonText(R.string.setting)
                .setDeniedMessage(R.string.permission_ncessary_setting)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        if (permissionGrantedListener != null) {
                            permissionGrantedListener.onPermissionDenied();
                        }
                    }

                }).check();
    }
}
