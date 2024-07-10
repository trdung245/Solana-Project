package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var changeAvatarButton: Button
    private lateinit var changeNameButton: Button
    private lateinit var changePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.profile)

        backButton = findViewById(R.id.backButton)
        changeAvatarButton = findViewById(R.id.changeAvatarButton)
        changeNameButton = findViewById(R.id.changeNameButton)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        backButton.setOnClickListener{
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }
    }
}