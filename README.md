# Android-CameraHelper

Using in Activity or in Fragment

```
CameraHelper cameraHelper = CameraHelper.newBuilder(this, PHOTO_REQUEST_CODE, GALLERY_REQUEST_CODE, CAMERA_PERMISSION_CODE, STORAGE_PERMISSION_CODE)
.build();

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
    String qwe = "";
  }
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  cameraHelper.onPermissionsResult(requestCode, permissions, grantResults);
}

```

