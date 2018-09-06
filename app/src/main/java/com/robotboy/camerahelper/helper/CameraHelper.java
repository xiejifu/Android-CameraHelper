package com.robotboy.camerahelper.helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

public class CameraHelper {
    //builder vars
    private FragmentActivity fragmentActivity;
    private Fragment fragment;
    private int photoRequestCode;
    private int galleryRequestCode;
    private int cameraPermissionsRequestCode;
    private int storagePermissionsRequestCode;
    private CameraErrorInterface errorInterface;
    //state vars
    private Uri photoUri = null;
    private String absolutePath = null;
    private boolean isSelfie;

    private CameraHelper() {

    }

    private FragmentActivity getContextFragmentActivity() {
        if (fragmentActivity != null) {
            return fragmentActivity;
        } else if (fragment != null) {
            FragmentActivity activity = fragment.getActivity();
            if (activity != null) {
                return activity;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void setCurrentImage(Uri uri, String path) {
        photoUri = uri;
        absolutePath = path;
    }

    public Uri getCurrentUri() {
        return photoUri;
    }

    private String getAbsolutePath() {
        return absolutePath;
    }

    private boolean checkCameraFeature(@NonNull Context context, @NonNull Intent testIntent) {
        PackageManager packageManager = context.getPackageManager();
        return (testIntent.resolveActivity(context.getPackageManager()) != null) &&
                (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA));
    }

    private File getInternalCameraFile(@NonNull Context context) {
        try {
            return CameraFileHelper.createInternalImageFile(context, CameraConstants.FILE_NAME_PREFIX, CameraConstants.FILE_NAME_EXTENSION);
        } catch (Throwable ignored) {

        }
        return null;
    }

    private void grantUriPermissions(@NonNull FragmentActivity activity, @NonNull Intent takePictureIntent, @NonNull Uri photoURI) {
        List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private Intent prepareCameraIntentForInternalStorage(FragmentActivity activity, boolean isSelfie) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            boolean cameraOk = checkCameraFeature(activity, takePictureIntent);
            File imageFile = getInternalCameraFile(activity);
            if (cameraOk && imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, CameraConstants.AUTHORITY, imageFile);
                setCurrentImage(photoURI, imageFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                grantUriPermissions(activity, takePictureIntent, photoURI);
                if (isSelfie) {
                    takePictureIntent.putExtra(CameraConstants.SELFIE_PARAM1, android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    takePictureIntent.putExtra(CameraConstants.SELFIE_PARAM2, true);
                }
                return takePictureIntent;
            } else {
                return null;
            }
        } catch (Throwable ignored) {
            // TODO error handling
        }
        return null;
    }

    private void startIntentForResult(Intent intent, int requestCode) throws CameraException {
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else if (fragmentActivity != null) {
            fragmentActivity.startActivityForResult(intent, requestCode);
        } else {
            throw new CameraException("No camera context exception");
        }
    }

    private Intent prepareGalleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType(CameraConstants.IMAGE_MIME_TYPE);
        return photoPickerIntent;
    }

    private void startCamera(FragmentActivity contextActivity, boolean selfie, int requestCode) throws CameraException {
        Intent takePictureIntent = prepareCameraIntentForInternalStorage(contextActivity, selfie);

        if (takePictureIntent != null) {
            startIntentForResult(takePictureIntent, requestCode);
        } else {
            throw new CameraException("Cant resolve activity for camera or fail create image file");
        }
    }

    @SuppressWarnings("PointlessBooleanExpression")
    public void startCameraWithPermission(boolean isSelfie) {
        this.isSelfie = isSelfie;
        try {
            CameraPermission permissions = new CameraPermission(fragmentActivity, fragment);
            FragmentActivity activityContext = getContextFragmentActivity();
            if (permissions.hasPermissionForCamera() == false) {
                permissions.requestPermissionsForCamera(cameraPermissionsRequestCode);
            } else if (permissions.hasPermissionForStorage() == false) {
                permissions.requestPermissionsForStorage(storagePermissionsRequestCode);
            } else {
                startCamera(activityContext, isSelfie, photoRequestCode);
            }
        } catch (Throwable error) {
            if (errorInterface != null) {
                errorInterface.onCameraError(error);
            }
        }
    }

    public void startPickingFromGallery() {
        Intent photoPickerIntent = prepareGalleryIntent();
        try {
            if (photoPickerIntent != null) {
                setCurrentImage(null, null);
                startIntentForResult(photoPickerIntent, galleryRequestCode);
            } else {
                if (errorInterface != null) {
                    errorInterface.onCameraError(new CameraException("No resolve activity for camera or fail create image file"));
                }
            }
        } catch (Throwable error) {
            if (errorInterface != null) {
                errorInterface.onCameraError(error);
            }
        }
    }

    public Bitmap getThumbnailFromCameraImage(Intent data) {
        Bundle extras = data.getExtras();
        return (Bitmap) extras.get(CameraConstants.THUMBNAIL_KEY);
    }

    private void addCameraPictureToGallery(Context context, Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (photoUri != null) {
            savedInstanceState.putString(CameraConstants.PHOTO_URI_STATE, photoUri.toString());
        }
        if (absolutePath != null) {
            savedInstanceState.putString(CameraConstants.PHOTO_PATH_STATE, absolutePath);
        }

        savedInstanceState.putBoolean(CameraConstants.IS_SELFIE_STATE, isSelfie);
        savedInstanceState.putInt(CameraConstants.CAMERA_PERMISSIONS_REQUEST_CODE, cameraPermissionsRequestCode);
        savedInstanceState.putInt(CameraConstants.STORAGE_PERMISSIONS_REQUEST_CODE, storagePermissionsRequestCode);
        savedInstanceState.putInt(CameraConstants.PHOTO_REQUEST_CODE, photoRequestCode);
        savedInstanceState.putInt(CameraConstants.GALLERY_REQUEST_CODE, galleryRequestCode);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CameraConstants.PHOTO_PATH_STATE)) {
                absolutePath = savedInstanceState.getString(CameraConstants.PHOTO_PATH_STATE);
            }
            if (savedInstanceState.containsKey(CameraConstants.PHOTO_URI_STATE)) {
                photoUri = Uri.parse(savedInstanceState.getString(CameraConstants.PHOTO_URI_STATE));
            }
            if (savedInstanceState.containsKey(CameraConstants.IS_SELFIE_STATE)) {
                isSelfie = savedInstanceState.getBoolean(CameraConstants.IS_SELFIE_STATE);
            }
            if (savedInstanceState.containsKey(CameraConstants.CAMERA_PERMISSIONS_REQUEST_CODE)) {
                cameraPermissionsRequestCode = savedInstanceState.getInt(CameraConstants.CAMERA_PERMISSIONS_REQUEST_CODE);
            }
            if (savedInstanceState.containsKey(CameraConstants.STORAGE_PERMISSIONS_REQUEST_CODE)) {
                storagePermissionsRequestCode = savedInstanceState.getInt(CameraConstants.STORAGE_PERMISSIONS_REQUEST_CODE);
            }
            if (savedInstanceState.containsKey(CameraConstants.PHOTO_REQUEST_CODE)) {
                photoRequestCode = savedInstanceState.getInt(CameraConstants.PHOTO_REQUEST_CODE);
            }
            if (savedInstanceState.containsKey(CameraConstants.GALLERY_REQUEST_CODE)) {
                galleryRequestCode = savedInstanceState.getInt(CameraConstants.GALLERY_REQUEST_CODE);
            }
        }
    }

    public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == cameraPermissionsRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && Manifest.permission.CAMERA.equals(permissions[0])) {
                startCameraWithPermission(isSelfie);
            }
        } else if (requestCode == storagePermissionsRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])) {
                startCameraWithPermission(isSelfie);
            }
        }
    }

    public File onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == photoRequestCode && resultCode == FragmentActivity.RESULT_OK) {
                return ImageHelper.handleCameraImage(getContextFragmentActivity(), getCurrentUri(), getAbsolutePath());
            } else if (requestCode == galleryRequestCode && resultCode == FragmentActivity.RESULT_OK) {
                return ImageHelper.handleGalleryImage(getContextFragmentActivity(), data.getData());
            }
        } catch (Throwable error) {
            if (errorInterface != null) {
                errorInterface.onCameraError(error);
            }
        }
        return null;
    }


    public static Builder newBuilder(FragmentActivity fragmentActivity, int photoRequestCode, int galleryRequestCode, int cameraPermissionRequestCode, int storagePermissionRequestCode) {
        return new CameraHelper().new Builder(fragmentActivity, photoRequestCode, galleryRequestCode, cameraPermissionRequestCode, storagePermissionRequestCode);
    }

    public static Builder newBuilder(Fragment fragment, int photoRequestCode, int galleryRequestCode, int cameraPermissionRequestCode, int storagePermissionRequestCode) {
        return new CameraHelper().new Builder(fragment, photoRequestCode, galleryRequestCode, cameraPermissionRequestCode, storagePermissionRequestCode);
    }

    public class Builder {

        private Builder(FragmentActivity fragmentActivity, int photoRequestCode, int galleryRequestCode, int cameraPermissionRequestCode, int storagePermissionRequestCode) {
            CameraHelper.this.fragmentActivity = fragmentActivity;
            CameraHelper.this.photoRequestCode = photoRequestCode;
            CameraHelper.this.galleryRequestCode = galleryRequestCode;

            CameraHelper.this.cameraPermissionsRequestCode = cameraPermissionRequestCode;
            CameraHelper.this.storagePermissionsRequestCode = storagePermissionRequestCode;
        }

        private Builder(Fragment fragment, int photoRequestCode, int galleryRequestCode, int cameraPermissionRequestCode, int storagePermissionRequestCode) {
            CameraHelper.this.fragment = fragment;
            CameraHelper.this.photoRequestCode = photoRequestCode;
            CameraHelper.this.galleryRequestCode = galleryRequestCode;

            CameraHelper.this.cameraPermissionsRequestCode = cameraPermissionRequestCode;
            CameraHelper.this.storagePermissionsRequestCode = storagePermissionRequestCode;
        }

        public Builder setErrorInterface(CameraErrorInterface errorInterface) {
            CameraHelper.this.errorInterface = errorInterface;
            return this;
        }

        public CameraHelper build() {
            return CameraHelper.this;
        }
    }
}
