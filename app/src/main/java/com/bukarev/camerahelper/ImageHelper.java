package com.bukarev.camerahelper;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ImageHelper {
    private static String FOLDER_SEPARATOR = "/";
    private static String READ_MODE = "r";

    public static File handleGalleryImage(Context context, Uri data) throws Exception {
        return createFileFromUri(context, data);
    }

    private static File createFileFromUri(Context context, Uri data) throws Exception {
        //DocumentFile file = DocumentFile.fromSingleUri(context,data);
        File file = new File(data.getPath());

        String fileName = file.getName();
        File fileCreated;
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(data, READ_MODE);
        InputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
        String filePath = context.getExternalCacheDir()
                + FOLDER_SEPARATOR
                + fileName;

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
