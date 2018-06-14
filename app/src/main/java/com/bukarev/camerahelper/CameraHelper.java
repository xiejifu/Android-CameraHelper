package com.bukarev.camerahelper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.List;

public class CameraHelper {
    private final FragmentActivity fragmentActivity;
    private final Fragment fragment;
    private final int photoRequestCode;
    private final int cameraRequestCode;
    private final int storageRequestCode;
    private Uri photoUri = null;
    private boolean isInternal;
    private boolean isSelfie;
    private final OnCameraResult errorInterface;

    public static CameraHelper createCameraHelpersWith(AppCompatActivity activity, OnCameraResult errorInterface, int photoRequestCode, int cameraRequestCode, int storageRequestCode) {
        return new CameraHelper(activity, null, errorInterface, photoRequestCode, cameraRequestCode, storageRequestCode);
    }

    public static CameraHelper createCameraHelpersWith(Fragment fragment, OnCameraResult errorInterface, int photoRequestCode, int cameraRequestCode, int storageRequestCode) {
        return new CameraHelper(null, fragment, errorInterface, photoRequestCode, cameraRequestCode, storageRequestCode);
    }

    private CameraHelper(AppCompatActivity activity, Fragment fragment, OnCameraResult errorInterface, int photoRequestCode, int cameraRequestCode, int storageRequestCode) {
        this.fragmentActivity = activity;
        this.fragment = fragment;
        this.photoRequestCode = photoRequestCode;
        this.cameraRequestCode = cameraRequestCode;
        this.storageRequestCode = storageRequestCode;
        this.errorInterface = errorInterface;
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

    private void setCurrentUri(Uri uri) {
        photoUri = uri;
    }

    public Uri getCurrentUri() {
        return photoUri;
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

    private File getExternalCameraFile(@NonNull Context context) {
        try {
            return CameraFileHelper.createExternalImageFile(context, CameraConstants.FILE_NAME_PREFIX, CameraConstants.FILE_NAME_EXTENSION);
        } catch (Throwable ignored) {

        }
        return null;
    }

    private void grantUriPermissionsForLollipop(@NonNull FragmentActivity activity, @NonNull Intent takePictureIntent, @NonNull Uri photoURI) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    private Intent prepareCameraIntentForInternalStorage(FragmentActivity activity, boolean isSelfie) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            boolean cameraOk = checkCameraFeature(activity, takePictureIntent);
            File imageFile = getInternalCameraFile(activity);
            if (cameraOk && imageFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, CameraConstants.AUTHORITY, imageFile);
                setCurrentUri(photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                grantUriPermissionsForLollipop(activity, takePictureIntent, photoURI);
                if (isSelfie) {
                    takePictureIntent.putExtra(CameraConstants.SELFIE_PARAM1, android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    takePictureIntent.putExtra(CameraConstants.SELFIE_PARAM2, true);
                }
                return takePictureIntent;
            } else {
                return null;
            }
        } catch (Throwable ignored) {

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

    private Intent prepareCameraIntentForExternalStorage(FragmentActivity activity, boolean isSelfie) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            boolean cameraOk = checkCameraFeature(activity, takePictureIntent);
            File imageFile = getExternalCameraFile(activity);
            if (cameraOk && imageFile != null) {
                Uri photoURI = Uri.fromFile(imageFile);
                setCurrentUri(photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                grantUriPermissionsForLollipop(activity, takePictureIntent, photoURI);
                if (isSelfie) {
                    takePictureIntent.putExtra(CameraConstants.SELFIE_PARAM1, android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
                    takePictureIntent.putExtra(CameraConstants.SELFIE_PARAM2, true);
                }
                return takePictureIntent;
            } else {
                return null;
            }
        } catch (Throwable ignored) {

        }
        return null;
    }

    private Intent prepareGalleryIntent() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType(CameraConstants.IMAGE_MIME_TYPE);
        return photoPickerIntent;
    }

    private void startCamera(FragmentActivity contextActivity, boolean internal, boolean selfie, int requestCode) throws CameraException {
        Intent takePictureIntent;
        if (internal)
            takePictureIntent = prepareCameraIntentForInternalStorage(contextActivity, selfie);
        else
            takePictureIntent = prepareCameraIntentForExternalStorage(contextActivity, selfie);

        if (takePictureIntent != null) {
            setCurrentUri(null);
            startIntentForResult(takePictureIntent, requestCode);
        } else {
            throw new CameraException("Cant resolve activity for camera or fail create image file");
        }
    }

    @SuppressWarnings("PointlessBooleanExpression")
    public void startCameraWithPermission(boolean internal, boolean selfie) {
        try {
            CameraPermission permissions = new CameraPermission(fragmentActivity, fragment);
            FragmentActivity activityContext = getContextFragmentActivity();
            if (permissions.hasPermissionForCamera() == false) {
                permissions.requestPermissionsForCamera(cameraRequestCode);
            } else if (internal) {
                startCamera(activityContext, internal, selfie, photoRequestCode);
            } else {
                if (permissions.hasPermissionForStorage() == false) {
                    permissions.requestPermissionsForStorage(storageRequestCode);
                } else {
                    startCamera(activityContext, internal, selfie, photoRequestCode);
                }
            }
        } catch (Throwable error) {
            if (errorInterface != null) {
                errorInterface.onCameraError(error);
            }
        }
    }

    public void startPickingFromGallery(int requestCode) throws CameraException {
        Intent photoPickerIntent = prepareGalleryIntent();

        if (photoPickerIntent != null) {
            setCurrentUri(null);
            startIntentForResult(photoPickerIntent, requestCode);
        } else {
            throw new CameraException("No resolve activity for camera or fail create image file");
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
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CameraConstants.PHOTO_URI_STATE)) {
                photoUri = Uri.parse(savedInstanceState.getString(CameraConstants.PHOTO_URI_STATE));
            }
        }
    }

    public void onPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == cameraRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && Manifest.permission.CAMERA.equals(permissions[0])) {
                startCameraWithPermission(isInternal, isSelfie);
            }
        } else if (requestCode == storageRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])) {
                startCameraWithPermission(isInternal, isSelfie);
            }
        }
    }

    public File onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == cameraRequestCode && resultCode == FragmentActivity.RESULT_OK) {
            ImageHelper.handleGalleryImage(getActivity(), cameraHelper.getCurrentUri(), 90)
                    .subscribe(this::startNext, throwable -> MessageHelpers.failCamera(getActivity()));
        } else if (requestCode == cameraRequestCode && resultCode == FragmentActivity.RESULT_OK) {
            ImageHelper.handleGalleryImage(getActivity(), cameraHelper.getCurrentUri(), 90)
                    .subscribe(this::startNext, throwable -> MessageHelpers.failCamera(getActivity()));
        }
    }
}
