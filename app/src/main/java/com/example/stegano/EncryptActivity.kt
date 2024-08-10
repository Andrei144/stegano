package com.example.stegano

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class EncryptActivity : AppCompatActivity() {

    private lateinit var message: String
    private lateinit var password: String
    private lateinit var imageUri: Uri

    private var image: Bitmap? = null
    private var coverImage: Bitmap? = null

//    private var managerAES = CryptoManagerAES()
    private var managerStegano: EncodeModel? = null

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
            img = ImageDecoder.decodeBitmap(
                                source,
                                ImageDecoder.OnHeaderDecodedListener{ decoder, _, _ ->
                                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                                    decoder.isMutableRequired = true
                                }
                                )
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

//    Radio group logic
    private fun getEncryptionAlgorithm(): String {
        val radioGroup = findViewById<RadioGroup>(R.id.encryptionRadioGroup)
        val selectedAlgorithm = radioGroup.checkedRadioButtonId

        when (selectedAlgorithm) {
            R.id.identical -> return "identical"
            R.id.AES -> return "AES"
//            R.id.RSA -> return "RSA"
        }

        return "AES" // Default value if no radio button is selected
    }

//    On click listeners for the buttons
    fun onClickEncrypt(view: View) {
        val messageBytes = message.encodeToByteArray()
        val passwordBytes = password.encodeToByteArray()

        managerStegano = EncodeModel()
//        var encryptedBytes: ByteArray
//
//        try {
//            encryptedBytes = managerAES.encrypt(bytes)
//        }catch (e: Exception){
//            managerAES = CryptoManagerAES()
//            encryptedBytes = managerAES.encrypt(bytes)
//        }
//
//        val encryptedMessage = encryptedBytes.decodeToString()
//        Log.d("DEV", "Encrypted message: $encryptedMessage\n\t$encryptedBytes")
//
//        val decryptedBytes = managerAES.decrypt(encryptedBytes)
//        val decryptedMessage = decryptedBytes.decodeToString()
//        Log.d("DEV", "Decrypted message: $decryptedMessage")

        coverImage = managerStegano?.encapsulate(messageBytes, passwordBytes, image!!)

        try {
            val hiddenBytes = managerStegano?.decapsulate(coverImage!!, passwordBytes)
            val hiddenMessage = hiddenBytes?.decodeToString()
            Log.d("DEV", "Hidden message: $hiddenMessage")
        }catch (e: Exception){
            if(e.message == "Incorrect password"){
                Log.d("DEV", "Password is not correct")
            }else{
                Log.d("DEV", e.message.toString())
            }
        }


//        TODO("Implement the encryption algorithms")
    }
    fun onClickSendMail(view: View) {
//        TODO("Implement the sending of the message via mail")
    }
    fun onClickSaveImageBtn(view: View) {
//        TODO("Implement the saving of the steganographed image")
    }
    fun onClickGoBack(view: View) {
        Log.d("DEV", "Go back button clicked")
        finish()
    }
}