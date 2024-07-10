package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class CommunityActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var facebookButton: ImageButton
    private lateinit var discordButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.community)

        backButton = findViewById(R.id.backButton)
        facebookButton = findViewById(R.id.facebookButton)
        discordButton = findViewById(R.id.discordButton)

        backButton.setOnClickListener{
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }
    }
}