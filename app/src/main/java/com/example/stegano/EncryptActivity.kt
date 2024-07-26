package com.example.stegano

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class EncryptActivity : AppCompatActivity() {

    private lateinit var message: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_encrypt)

        password = intent.getStringExtra("password").toString()
        message = intent.getStringExtra("message").toString()

    }

    fun onClickEncrypt() {
        Log.d("DEV", "Message: $message \t\tPassword: $password")
    }

    fun onClickSendMail() {}
    fun onClickSaveImageBtn() {}
}