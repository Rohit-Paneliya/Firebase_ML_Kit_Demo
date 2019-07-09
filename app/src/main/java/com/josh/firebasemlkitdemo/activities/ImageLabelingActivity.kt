package com.josh.firebasemlkitdemo.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.josh.firebasemlkitdemo.R
import com.josh.firebasemlkitdemo.utils.ImagePicker
import kotlinx.android.synthetic.main.activity_text_recognizing_acivity.*
import pub.devrel.easypermissions.EasyPermissions

class ImageLabelingActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognizing_acivity)

        buttonAddImage.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val perms = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (EasyPermissions.hasPermissions(this, *perms)) {
            startActivityForResult(ImagePicker.getPickImageIntent(this), REQUEST_CODE_IMAGE)
        } else {
            EasyPermissions.requestPermissions(
                this, getString(R.string.msg_runtime_permission),
                REQUEST_CODE_PERMISSION, *perms
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        selectImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_IMAGE -> {

                    textViewResult.text = ""
                    val externalFile2 = ImagePicker.getImageFromResult(this, resultCode, data)
                    val uri = Uri.parse(externalFile2.absolutePath)
                    imageView.setImageURI(uri)
                    val bitmap = BitmapFactory.decodeFile(externalFile2.path)
                    callTextRecognition(bitmap)
                }
            }
        }

    }

    /*
    *   This function is used to do the setup for the text recognizing.
    * */
    private fun callTextRecognition(imageBitmap: Bitmap) {
        val capturedImage = FirebaseVisionImage.fromBitmap(imageBitmap)
        imageLabeler(capturedImage)
    }

    private fun imageLabeler(capturedImage: FirebaseVisionImage) {
        val imageLabeler = FirebaseVision.getInstance().onDeviceImageLabeler

        imageLabeler.processImage(capturedImage).addOnSuccessListener {
            var result = ""
            for (label in it) {
                val text = label.text
                result += text
            }
            textViewResult.text = result
        }
    }
}
