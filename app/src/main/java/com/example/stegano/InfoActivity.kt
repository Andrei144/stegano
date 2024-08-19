package com.example.stegano

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class InfoActivity : AppCompatActivity() {

    private val useExplain = "\n   The app has 2 major functionalities:\n" +
            "    1) Creating a cover image (that has the next steps)\n" +
            "          1. Chose the cover image for your message\n" +
            "          2. Write down the message\n" +
            "          3. Chose a password\n" +
            "          4. Encapsulate & encode (the message is encrypted and then is used steganography)\n" +
            "          5* Save the cover image in the phone gallery\n" +
            "          5* Send the cover image via email\n" +
            "    2) Recover message out of the cover image\n" +
            "          1. Select the cover image\n" +
            "          2. Enter the encryption password\n" +
            "          3. Reveal the hidden message\n"
    private val steganographyExplain = "\n   Steganography is essentially a set of techniques for " +
            "hiding messages in different media types so that the data is invisible to everyone " +
            "excepting the sender and the receiver of the message.\n   " +
            "In this app, the media used as cover is the image that the user is selecting.\n"
    private val cryptographyExplain = "\n   Cryptography is focussed on protecting the data by " +
            "applying a set of 2 transformations. The first transformation is called encryption " +
            "and is used to make something unreadable out of data, while the second " +
            "transformation one is used to recover data from the encrypted data.\n   " +
            "The app is using encryption to add an extra layer of security.\n"

    private lateinit var useExplainTextView: TextView
    private lateinit var steganographyExplainTextView: TextView
    private lateinit var cryptographyExplainTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_info)

        useExplainTextView = findViewById(R.id.useExplainTextView)
        useExplainTextView.text = useExplain

        steganographyExplainTextView = findViewById(R.id.steganographyExplainTextView)
        steganographyExplainTextView.text = steganographyExplain

        cryptographyExplainTextView = findViewById(R.id.cryptographyExplainTextView)
        cryptographyExplainTextView.text = cryptographyExplain
    }

    fun onClickGoBack(view: View) {
        finish()
    }
}