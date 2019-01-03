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
     * 카메라 퍼미션
     *
     * @param context
     * @param permissionGrantedListener
     */
    public void checkCameraPermission(final Context context, final PermissionGrantedListener permissionGrantedListener) {
        TedPermission.with(context)
                .setGotoSettingButtonText(R.string.setting)
                .setDeniedMessage(R.string.permission_ncessary_setting)
                .setPermissions(Manifest.permission.CAMERA)
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

    /**
     * 휴대폰 상태 접근 퍼미션
     *
     * @param context
     * @param permissionGrantedListener
     */
    public void checkPhoneStatePermission(final Context context, final PermissionGrantedListener permissionGrantedListener) {
        TedPermission.with(context)
                .setGotoSettingButtonText(R.string.setting)
                .setDeniedMessage(R.string.permission_ncessary_setting)
                .setPermissions(Manifest.permission.READ_PHONE_STATE)
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
