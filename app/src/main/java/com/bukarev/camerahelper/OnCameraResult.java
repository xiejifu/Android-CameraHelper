package com.bukarev.camerahelper;

import java.io.File;

public interface OnCameraResult {
    void onCameraError(Throwable error);

    void onCapture(File image);
}
