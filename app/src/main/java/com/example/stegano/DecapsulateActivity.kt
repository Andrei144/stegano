package com.example.stegano

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DecapsulateActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_decapsulate)
    }

    fun onClickUploadToDecap(view: View) {}
    fun onClickDecapMessage(view: View) {}

}