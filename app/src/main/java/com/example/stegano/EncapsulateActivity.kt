package com.example.stegano

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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
    private var imageChosen = false

     private val getGalleryPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri: Uri? ->

        if (uri != null) {
            imageURI = uri
            imageView.setImageURI(imageURI)

            imageChosen = true
        }
    }

    private val getCameraPhoto = registerForActivityResult(ActivityResultContracts.TakePicture()){
        imageView.setImageURI(photoUri)
        imageURI = photoUri
        imageChosen = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_encapsulate)

        imageURI = null
        photoUri = getImageURI()
        imageChosen = false

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
        return this.message.isNotEmpty() && this.password.isNotEmpty() && imageChosen
    }

    private fun getImageURI():Uri{
        val image = File(filesDir, "camera_photos.jpg")
        return FileProvider.getUriForFile(this,
            "com.example.stegano.FileProvider", image)
    }

    fun onClickUploadPhoto(view: View) {
        getGalleryPhoto.launch("image/*")
    }

    fun onClickTakePhoto(view: View) {
        getCameraPhoto.launch(photoUri)
    }

    fun onClickEncapsulate(view: View) {
        if(this.inputProvided()){

            Intent(this, EncryptActivity::class.java).also {
                it.putExtra("password", password)
                it.putExtra("message", message)
                it.putExtra("imageURI", imageURI.toString())

                startActivity(it)
            }
        }else{
            Toast.makeText(this, "Please provide all the data required",
                Toast.LENGTH_LONG).show()
        }
    }

    fun onClickGoBack(view: View) {
        finish()
    }
}