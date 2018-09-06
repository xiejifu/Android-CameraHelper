package com.robotboy.camerahelper.app

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.robotboy.camerahelper.helper.CameraErrorInterface
import com.robotboy.camerahelper.helper.CameraHelper
import com.robotboy.camerahelper.R

class MainActivity : AppCompatActivity(), CameraErrorInterface {
    lateinit var cameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cameraBtn = findViewById<Button>(R.id.camera)
        cameraHelper = CameraHelper.newBuilder(this, 1, 2, 3, 4).setErrorInterface(this).build()
        cameraBtn.setOnClickListener {
            cameraHelper.startCameraWithPermission(false)
        }
    }

    override fun onCameraError(error: Throwable?) {
        // TODO show error
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imageFile = cameraHelper.onActivityResult(requestCode, resultCode, data)
            val imageView = findViewById<SubsamplingScaleImageView>(R.id.image)
            imageView.setImage(ImageSource.uri(cameraHelper.currentUri))
        }
    }
}
