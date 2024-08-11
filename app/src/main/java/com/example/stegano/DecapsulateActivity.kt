package com.example.stegano

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class DecapsulateActivity: AppCompatActivity() {

    private var imageURI : Uri? = null
    private var imageChosen = false

    private var managerStegano: EncodeModel? = null

    private lateinit var encryptionPassword: EditText
    private lateinit var password: String
    private lateinit var imageView: ImageView
    private lateinit var messageView: TextView

    private var decapsulateMessageInfo: String = "The decapsulated message will be displayed here."

    private val getGalleryPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri: Uri? ->

        if (uri != null) {
            imageURI = uri
            imageView.setImageURI(imageURI)

            imageChosen = true
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_decapsulate)

        imageView = findViewById(R.id.imageViewDecap)
        encryptionPassword = findViewById(R.id.editTextPasswordDecap)
        messageView = findViewById(R.id.textView5)

        messageView.text = decapsulateMessageInfo
    }

    fun onClickUploadToDecap(view: View) {
        getGalleryPhoto.launch("image/*")

        messageView.text = decapsulateMessageInfo
    }

    fun onClickDecapMessage(view: View) {
        password = encryptionPassword.text.toString()
        managerStegano = EncodeModel()

        if(password.isEmpty()){
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_LONG).show()
            return
        }

        if(!imageChosen){
            Toast.makeText(this, "A photo must be uploaded", Toast.LENGTH_LONG).show()
            return
        }

        val passwordBytes = password.encodeToByteArray()
        val coverImage = imageFromImageURI(imageURI!!)

        try {
            val hiddenBytes = managerStegano?.decapsulate(coverImage, passwordBytes)
            val hiddenMessage = hiddenBytes?.decodeToString()

            messageView.text = hiddenMessage

            Log.d("DEV", "Hidden message: $hiddenMessage")
        }catch (e: Exception){
            if(e.message == "Incorrect password"){
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
                Log.d("DEV", e.message.toString())
            }
        }
    }

    fun onClickGoBack(view: View) {
        messageView.text = decapsulateMessageInfo

        finish()
    }
}