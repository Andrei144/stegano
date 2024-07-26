package com.example.stegano

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class EncapsulateActivity: AppCompatActivity() {

    private lateinit var messageInput: TextInputEditText
    private lateinit var encryptionPassword: EditText
    private lateinit var imageView: ImageView


    private lateinit var message: String
    private lateinit var password: String
//    private lateinit var image: Image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_encapsulate)

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

    fun onClickUploadPhoto() {
        Log.d("DEV", "Upload photo button clicked")
//        TODO("Upload photo")
    }
    fun onClickTakePhoto() {
        Log.d("DEV", "Take photo button clicked")
//        TODO("Take photo")
    }
    fun onClickEncapsulate() {
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
}