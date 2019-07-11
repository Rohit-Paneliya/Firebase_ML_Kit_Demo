package com.josh.firebasemlkitdemo.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.josh.firebasemlkitdemo.R
import com.josh.firebasemlkitdemo.utils.ImagePicker
import kotlinx.android.synthetic.main.activity_text_recognizing_acivity.*
import pub.devrel.easypermissions.EasyPermissions

const val REQUEST_CODE_IMAGE = 111
const val REQUEST_CODE_PERMISSION = 112

class TextRecognizingActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognizing_acivity)

        //TODO UI improvement and image rotation functionality

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
        val textDetector = FirebaseVision.getInstance().onDeviceTextRecognizer
        //imageLabeler(capturedImage)

        textDetector.processImage(capturedImage)
            .addOnSuccessListener { texts -> getTextualResults(texts) }
            .addOnFailureListener { e -> e.printStackTrace() }
    }

    /*
    *   This function is used to show the text.
    * */
    private fun getTextualResults(texts: FirebaseVisionText) {
        val blocks = texts.textBlocks

        if (blocks.size == 0) {
            Toast.makeText(this, "No Text Found!", Toast.LENGTH_SHORT).show()
        } else {
            var recognizedText = ""

            for (block in blocks) {
                recognizedText += block.text + "\n"
            }

            textViewResult.text = recognizedText
        }
    }

//    private fun imageLabeler(capturedImage: FirebaseVisionImage) {
//        val imageLabeler = FirebaseVision.getInstance().onDeviceImageLabeler
//
//        imageLabeler.processImage(capturedImage).addOnSuccessListener {
//            var result = ""
//            for (label in it) {
//                val text = label.text
//                result += text
//            }
//            textViewResult.text = result
//        }
//    }

//    private fun getTextualResults(texts: FirebaseVisionText) {
//        val blocks = texts.textBlocks
//        if (blocks.size == 0) {
//            Toast.makeText(this, "No Text Found", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        var recognizedText = ""
//
//        for (block in blocks) {
//            val blockText = block.text
//            val blockConfidence = block.confidence
//            val blockLanguages = block.recognizedLanguages
//            val blockCornerPoints = block.cornerPoints
//            val blockFrame = block.boundingBox
//            recognizedText+=blockText+"\n"
//
//
//            for (line in block.lines) {
//                val lineText = line.text
//                val lineConfidence = line.confidence
//                val lineLanguages = line.recognizedLanguages
//                val lineCornerPoints = line.cornerPoints
//                val lineFrame = line.boundingBox
//                for (element in line.elements) {
//                    val elementText = element.text
//                    val elementConfidence = element.confidence
//                    val elementLanguages = element.recognizedLanguages
//                    val elementCornerPoints = element.cornerPoints
//                    val elementFrame = element.boundingBox
//
//                }
//            }
//        }
//
//        textViewResult.text = recognizedText
//    }
}
