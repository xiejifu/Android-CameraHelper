package com.bukarev.camerahelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity implements OnCameraResult {
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraHelper = CameraHelper.newBuilder(this, 1, 2, 3, 4)
                .setErrorInterface(this)
                .build();
        cameraHelper.startCameraWithPermission(true, false);
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
        File file = cameraHelper.onActivityResult(requestCode, resultCode, data);
        String qwe = "";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraHelper.onPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCameraError(Throwable error) {

    }
}
