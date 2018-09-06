package com.robotboy.camerahelper.helper;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ImageHelper {

    public static File handleCameraImage(Context context, Uri data, String path) throws Exception {
        return new File(path);
        //return createFileFromUri(context, data);
    }

    public static File handleGalleryImage(Context context, Uri data) throws Exception {
        return createFileFromUri(context, data);
    }

    private static File createFileFromUri(Context context, Uri data) throws Exception {
        String fileName = CameraFileHelper.generateUUIDFileName(CameraConstants.FILE_NAME_PREFIX);

        File fileCreated;
        InputStream inputStream = context.getContentResolver().openInputStream(data);
        String FOLDER_SEPARATOR = "/";
        String filePath = context.getExternalCacheDir() + FOLDER_SEPARATOR + fileName;

        if (!createFile(filePath)) {
            return new File(filePath);
        }

        ReadableByteChannel from = Channels.newChannel(inputStream);
        WritableByteChannel to = Channels.newChannel(new FileOutputStream(filePath));
        fastChannelCopy(from, to);
        from.close();
        to.close();
        fileCreated = new File(filePath);
        return fileCreated;
    }

    private static boolean createFile(String path) throws IOException {
        if (!checkExistence(path)) {
            File temp = new File(path);
            if (!temp.createNewFile()) {
            } else {
            }
        } else {
            return false;
        }
        return true;
    }

    private static boolean checkExistence(String path) {
        File temp = new File(path);
        return temp.exists();
    }

    private static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

}
