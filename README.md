# Android-CameraHelper
Small library camera assistant.

### Using in Activity or in Fragment

```java
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
  }
}

@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  cameraHelper.onPermissionsResult(requestCode, permissions, grantResults);
}

```

Licence
-----------

Copyright 2018 Sergey Bukarev

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

