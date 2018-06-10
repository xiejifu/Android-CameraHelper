package com.bukarev.camerahelper;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.UUID;

public class CameraFileHelper {

    private static String generateUUIDFileName(String prefix) {
        String code = UUID.randomUUID().toString();
        code = code.replace("-", "");
        code = code.replace(" ", "");
        code = code.replace("_", "");

        return prefix + "_" + code + "_";
    }

    public static File createInternalImageFile(Context context, String prefix, String ext) throws Throwable {
        String imageFileName = generateUUIDFileName(prefix);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            boolean ignore = storageDir.mkdirs();
            return File.createTempFile(imageFileName, "." + ext, storageDir);
        } else {
            return null;
        }
    }

    public static File createExternalImageFile(Context context, String prefix, String ext) throws Throwable {
        String imageFileName = generateUUIDFileName(prefix);

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null) {
            boolean ignore = storageDir.mkdirs();
            return File.createTempFile(imageFileName, "." + ext, storageDir);
        } else {
            return null;
        }
    }
}
