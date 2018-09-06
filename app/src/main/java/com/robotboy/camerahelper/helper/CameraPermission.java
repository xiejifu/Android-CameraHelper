package com.robotboy.camerahelper.helper;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.robotboy.camerahelper.R;

public class CameraPermission {
    private final FragmentActivity activity;
    private final Fragment fragment;

    CameraPermission(FragmentActivity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    //region camera
    public boolean hasPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionsForCamera(final int resultCode) {
        Context context = getContext();
        if (isNeedCameraExplanations()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.app_name)
                    .setMessage(R.string.camera_need_explanations)
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setNegativeButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    requestPermissions(activity, fragment, new String[]{Manifest.permission.CAMERA}, resultCode);
                                }
                            })
                    .create().show();
        } else {
            requestPermissions(activity, fragment, new String[]{Manifest.permission.CAMERA}, resultCode);
        }
    }

    private boolean isNeedCameraExplanations() {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
    }
    //endregion

    //region storage
    public boolean hasPermissionForStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionsForStorage(final int resultCode) {
        if (isNeedStorageExplanations()) {
            Context context = getContext();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.app_name)
                    .setMessage(R.string.storage_need_explanations)
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setNegativeButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    requestPermissions(activity, fragment, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, resultCode);
                                }
                            })
                    .create().show();
        } else {
            requestPermissions(activity, fragment, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, resultCode);
        }
    }

    private boolean isNeedStorageExplanations() {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    //endregion

    private Context getContext() {
        if (activity != null) {
            return activity;
        } else if (fragment != null) {
            return fragment.getActivity();
        } else {
            return null;
        }
    }

    private static void requestPermissions(FragmentActivity activity, Fragment fragment, String[] permissions, int requestCode) {
        if (activity != null) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else if (fragment != null) {
            fragment.requestPermissions(permissions, requestCode);
        }
    }
}
