package com.bukarev.camerahelper.Example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.bukarev.camerahelper.CameraHelper.CameraErrorInterface;
import com.bukarev.camerahelper.CameraHelper.CameraHelper;
import com.bukarev.camerahelper.R;

import java.io.File;

public class MainActivity extends AppCompatActivity implements CameraErrorInterface {
    private CameraHelper cameraHelper;
    private static final int PHOTO_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION_CODE = 3;
    private static final int STORAGE_PERMISSION_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraHelper = CameraHelper.newBuilder(this, PHOTO_REQUEST_CODE, GALLERY_REQUEST_CODE, CAMERA_PERMISSION_CODE, STORAGE_PERMISSION_CODE)
                .setErrorInterface(this)
                .build();
        //cameraHelper.startCameraWithPermission(true, false);
        cameraHelper.startPickingFromGallery();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        cameraHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cameraHelper.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PHOTO_REQUEST_CODE || requestCode == GALLERY_REQUEST_CODE) && resultCode == RESULT_OK) {
            File file = cameraHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraHelper.onPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCameraError(Throwable error) {
        // handle error
    }
}
