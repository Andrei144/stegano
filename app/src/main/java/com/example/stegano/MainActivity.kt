package com.example.stegano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

    }

    fun onEncBtnClick(view: View) {
        Intent(applicationContext, EncapsulateActivity::class.java).also {
            startActivity(it)
        }
    }

    fun onDecBtnClick(view: View) {
        Intent(applicationContext, DecapsulateActivity::class.java).also {
            startActivity(it)
        }
    }

    fun onClickMoreInfo(view: View) {
        Intent(applicationContext, InfoActivity::class.java).also {
            startActivity(it)
        }
    }

    fun onClickGoBack(view: View) {
        finish()
    }
}