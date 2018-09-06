package com.robotboy.camerahelper.helper;


public class CameraConstants {
    private final static String PACKAGE = "com.robotboy.camerahelper";
    public final static String PHOTO_URI_STATE = PACKAGE + ".PHOTO_URI_STATE";
    public final static String PHOTO_PATH_STATE = PACKAGE + ".PHOTO_PATH_STATE";
    public final static String IS_SELFIE_STATE = PACKAGE + ".IS_SELFIE_STATE";
    public final static String CAMERA_PERMISSIONS_REQUEST_CODE = PACKAGE + ".CAMERA_PERMISSIONS_REQUEST_CODE";
    public final static String STORAGE_PERMISSIONS_REQUEST_CODE = PACKAGE + ".STORAGE_PERMISSIONS_REQUEST_CODE";
    public final static String PHOTO_REQUEST_CODE = PACKAGE + ".PHOTO_REQUEST_CODE";
    public final static String GALLERY_REQUEST_CODE = PACKAGE + ".GALLERY_REQUEST_CODE";

    public final static String AUTHORITY = PACKAGE + ".fileprovider";
    public final static String SELFIE_PARAM1 = "android.intent.extras.CAMERA_FACING";
    public final static String SELFIE_PARAM2 = "android.intent.extra.USE_FRONT_CAMERA";
    public static final String FILE_NAME_PREFIX = "CAMERA";
    public static final String FILE_NAME_EXTENSION = "JPG";
    public static final String IMAGE_MIME_TYPE = "image/*";
    public static final String THUMBNAIL_KEY = "data";
}