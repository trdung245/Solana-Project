// src/main/java/com/example/myapplication/ui/MainActivity.kt
package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.data.model.User
import com.example.myapplication.data.network.ApiClient
import com.example.myapplication.data.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.data.solana.SolanaUtils

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton1: Button
    private lateinit var registerButton2: Button
    private lateinit var continueButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.loginbtn)
        registerButton1 = findViewById(R.id.registerbtn1)

        registerButton1.setOnClickListener {
            setContentView(R.layout.register)

            registerButton2 = findViewById(R.id.registerbtn2)
            registerButton2.setOnClickListener {
                val username = usernameInput.text.toString()
                val password = passwordInput.text.toString()
                Log.i("Test Credentials", "Username: $username and Password: $password")

                fetchData()

                val newWalletAddress = SolanaUtils.createNewWallet()
                Log.i("Test Credentials", "New Wallet Address: $newWalletAddress")

                SolanaUtils.fetchWalletBalance(newWalletAddress)

                setContentView(R.layout.activity_key_display)

                val publicKeyTextView: TextView = findViewById(R.id.public_key_text_view)

                publicKeyTextView.text = newWalletAddress

                continueButton = findViewById(R.id.continueButton)
                continueButton.setOnClickListener {
                    val intent = Intent(this, MainMenuActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            Log.i("Test Credentials", "Username: $username and Password: $password")

            // Fetch data and proceed
            fetchData()

            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchData() {
        val apiService = ApiClient.getRetrofitInstance().create(ApiService::class.java)
        val call = apiService.users

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body()
                    userList?.forEach { user ->
                        Log.d("User", "ID: ${user.id}, Name: ${user.name}, Email: ${user.email}")
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.e("Error", t.message ?: "Unknown error")
            }
        })
    }
}
