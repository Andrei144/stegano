package com.example.stegano

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
//import android.widget.Button
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

    /**
     * A native method that is implemented by the 'stegano' native library,
     * which is packaged with this application.
     */
//    external fun stringFromJNI(): String
//
//    companion object {
//        // Used to load the 'stegano' library on application startup.
//        init {
//            System.loadLibrary("stegano")
//        }
//    }
}