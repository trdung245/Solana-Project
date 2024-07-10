package com.example.myapplication.ui;

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class MainMenuActivity : AppCompatActivity() {

    private lateinit var captureButton: ImageButton
    private lateinit var eventButton: ImageButton
    private lateinit var balanceButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var communityButton: ImageButton
    private lateinit var settingButton: ImageButton

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_menu)

        captureButton = findViewById(R.id.capture_button)
        eventButton = findViewById(R.id.event_button)
        balanceButton = findViewById(R.id.balance_button)
        profileButton = findViewById(R.id.profile_button)
        communityButton = findViewById(R.id.community_button)
        settingButton = findViewById(R.id.setting_button)

        captureButton.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        eventButton.setOnClickListener{
            val intent = Intent(this, EventActivity::class.java)
            startActivity(intent)
        }

        balanceButton.setOnClickListener{
                val intent = Intent(this, BalanceActivity::class.java)
                startActivity(intent)
            }

        profileButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        communityButton.setOnClickListener{
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        settingButton.setOnClickListener{
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}
