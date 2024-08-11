package com.example.stegano

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class EncryptActivity : AppCompatActivity() {

    private lateinit var message: String
    private lateinit var password: String
    private lateinit var imageUri: Uri

    private var image: Bitmap? = null
    private var coverImage: Bitmap? = null

    private var managerStegano: EncodeModel? = null

    private lateinit var displayImageTextView: TextView
    private lateinit var coverImageView : ImageView
    private var textImageResultOriginal: String = "Cover image before steganography"
    private var textImageResultSteganographed: String = "Cover image after steganography"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_encrypt)

        displayImageTextView = findViewById(R.id.textViewImageResult)
        displayImageTextView.text = textImageResultOriginal
        coverImageView = findViewById(R.id.coverImageView)

        if(this.getData()){
            Log.d("DEV", "Data received successfully")
            coverImageView.setImageBitmap(image)
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
            img = ImageDecoder.decodeBitmap(
                                source,
                                ImageDecoder.OnHeaderDecodedListener{ decoder, _, _ ->
                                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                    decoder.isMutableRequired = true
                                }
                                )
        } else {
            // Use BitmapFactory for older versions
            @Suppress("DEPRECATION")
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
    private fun refreshImage(){
        displayImageTextView.text = textImageResultSteganographed
        coverImageView.setImageBitmap(coverImage)
        Toast.makeText(this, "Image was steganographed", Toast.LENGTH_LONG).show()
    }
    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        val filename = "${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/$folderName")
            }

            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { resolver.openOutputStream(it) }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + File.separator + folderName
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdirs()
            }
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

//    On click listeners for the buttons
    fun onClickEncode(view: View) {
        val messageBytes = message.encodeToByteArray()
        val passwordBytes = password.encodeToByteArray()

        managerStegano = EncodeModel()

        coverImage = managerStegano?.encapsulate(messageBytes, passwordBytes, image!!)
        refreshImage()

//        try {
//            val hiddenBytes = managerStegano?.decapsulate(coverImage!!, passwordBytes)
//            val hiddenMessage = hiddenBytes?.decodeToString()
//            Log.d("DEV", "Hidden message: $hiddenMessage")
//        }catch (e: Exception){
//            if(e.message == "Incorrect password"){
//                Log.d("DEV", "Password is not correct")
//            }else{
//                Log.d("DEV", e.message.toString())
//            }
//        }
    }
    fun onClickSendMail(view: View) {
//        TODO("Implement the sending of the message via mail")
    }

    fun onClickSaveImageBtn(view: View) {
        val folderName = "Stegano"

        if(coverImage == null){
            Toast.makeText(this, "Image is not steganographed", Toast.LENGTH_LONG).show()
            return
        }else {
            saveImage(coverImage!!, this, folderName)
        }

        Toast.makeText(this, "Image was saved", Toast.LENGTH_LONG).show()
    }

    fun onClickGoBack(view: View) {
        Log.d("DEV", "Go back button clicked")
        finish()
    }
}