package com.example.stegano

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class EncryptActivity : AppCompatActivity() {

    private lateinit var message: String
    private lateinit var password: String
    private lateinit var imageUri: Uri

    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_encrypt)

        if(this.getData()){
            Log.d("DEV", "Data received successfully")
        }else{
            Log.d("DEV", "Data not received")
            this.finish()
        }
    }

//    Functions to receive data from the previous activity
    @Override
    @Suppress("SameParameterValue")
    private fun getUriExtra(key: String): Uri {
        return Uri.parse(getStringExtra(key))
    }
    @Override
    private fun getStringExtra(key: String): String {
        return intent.getStringExtra(key).toString()
    }
    private fun imageFromImageURI(uri: Uri): Bitmap {
        lateinit var img: Bitmap

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            img = ImageDecoder.decodeBitmap(source)
        } else {
            // Use BitmapFactory for older versions
            img = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
        return img
    }
    @Override
    private fun checkData(): Boolean {
        return this.message.isNotEmpty() && this.password.isNotEmpty() && image != null
    }
    @Override
    private fun getData(): Boolean {
        password = getStringExtra("password")
        message = getStringExtra("message")
        imageUri = getUriExtra("imageURI")

        Log.d("DEV", "Password: $password\tMessage: $message\tImageURI: $imageUri")

        image = imageFromImageURI(imageUri)

        return this.checkData()
    }

//    On click listeners for the buttons
    fun onClickEncrypt(view: View) {
        TODO("Implement the encryption algorithms")
    }
    fun onClickSendMail(view: View) {
        TODO("Implement the sending of the message via mail")
    }
    fun onClickSaveImageBtn(view: View) {
        TODO("Implement the saving of the steganographed image")
    }
    fun onClickGoBack(view: View) {
        Log.d("DEV", "Go back button clicked")
        finish()
    }
}