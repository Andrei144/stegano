package com.example.stegano

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class EncapsulateActivity: AppCompatActivity() {

    private lateinit var messageInput: TextInputEditText
    private lateinit var encryptionPassword: EditText

    private lateinit var message: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_encapsulate)

        messageInput = findViewById(R.id.messageInput)
        encryptionPassword = findViewById(R.id.encryptionPassword)
    }

    private fun takeInputData() {
        this.message = this.messageInput.text.toString()
        this.password = this.encryptionPassword.text.toString()
    }

    private fun inputProvided(): Boolean {
        this.takeInputData()
        return this.message.isNotEmpty() && this.password.isNotEmpty()
    }

    fun onSendMailBtnClick(view: View) {
        Log.d("DEV", "Send mail button clicked")
//        TODO("Send mail")
    }

    fun onClickUploadPhoto(view: View) {
        Log.d("DEV", "Upload photo button clicked")
//        TODO("Upload photo")
    }
    fun onClickTakePhoto(view: View) {
        Log.d("DEV", "Take photo button clicked")
//        TODO("Take photo")
    }
    fun onClickEncapsulate(view: View) {
        if(this.inputProvided()){
            Log.d("DEV", "Message: $message \t\tPassword: $password")
        }else{
            Log.d("DEV", "Input not provided")
        }

        Log.d("DEV", "Encapsulate button clicked")
//        TODO("Encapsulate message")
    }
}