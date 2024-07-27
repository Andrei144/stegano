package com.example.stegano

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
//import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class EncapsulateActivity: AppCompatActivity() {

    private var imageURI : Uri? = null
    private lateinit var photoUri: Uri

    private lateinit var messageInput: TextInputEditText
    private lateinit var encryptionPassword: EditText
    private lateinit var imageView: ImageView


    private lateinit var message: String
    private lateinit var password: String
    private lateinit var image: Bitmap

     private val getGalleryPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri: Uri? ->

        if (uri != null) {
            imageURI = uri
            imageView.setImageURI(imageURI)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                image = ImageDecoder.decodeBitmap(source)
            } else {
                // Use BitmapFactory for older versions
                image = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        }
    }

    private val getCameraPhoto = registerForActivityResult(ActivityResultContracts.TakePicture()){
        imageView.setImageURI(photoUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_encapsulate)

        photoUri = getImageURI()

        messageInput = findViewById(R.id.messageInput)
        encryptionPassword = findViewById(R.id.encryptionPassword)
        imageView = findViewById(R.id.imageView)
    }

    private fun takeInputData() {
        this.message = this.messageInput.text.toString()
        this.password = this.encryptionPassword.text.toString()
    }

    private fun inputProvided(): Boolean {
        this.takeInputData()
        return this.message.isNotEmpty() && this.password.isNotEmpty()
    }

    private fun getImageURI():Uri{
        val image = File(filesDir, "camera_photos.jpg")
        return FileProvider.getUriForFile(this,
            "com.example.stegano.FileProvider", image)
    }

    fun onClickUploadPhoto(view: View) {
        Log.d("DEV", "Upload photo button clicked")

        getGalleryPhoto.launch("image/*")
    }

    fun onClickTakePhoto(view: View) {
        Log.d("DEV", "Take photo button clicked")
//        TODO("Take photo")

        getCameraPhoto.launch(photoUri)
    }

    fun onClickEncapsulate(view: View) {
        if(this.inputProvided()){
            Log.d("DEV", "Message: $message \t\tPassword: $password")

            Intent(this, EncryptActivity::class.java).also {
                it.putExtra("password", password)
                it.putExtra("message", message)

                startActivity(it)
            }
        }else{
            Log.d("DEV", "Input not provided")
        }

        Log.d("DEV", "Encapsulate button clicked")
//        TODO("Encapsulate message")
    }

    fun onClickGoBack(view: View) {
        finish()
    }
}