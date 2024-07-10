package com.example.myapplication.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException



class CameraActivity : AppCompatActivity() {

    private lateinit var ivUser: ImageView
    private lateinit var btnTakePicture: Button
    private lateinit var backButton: ImageButton

    // Define ActivityResultLauncher for taking a picture
    private lateinit var takePictureLauncher: ActivityResultLauncher<Void?>

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1
    }

    private val client = OkHttpClient()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_activity) // Ensure the layout name matches your XML file

        ivUser = findViewById(R.id.ivUser)
        btnTakePicture = findViewById(R.id.btnTakePicture)
        backButton = findViewById(R.id.backButton) // Initialize the back button

        // Register the launcher for the result of taking a picture
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicturePreview()
        ) { bitmap: Bitmap? ->
            bitmap?.let {
                ivUser.setImageBitmap(it) // Display the captured image
                val imageFile = saveBitmapToFile(this, it, "captured_image")
                imageFile?.let { file ->
                    uploadImageToIPFS(file)
                    sendIpfsUrlToServerForComparison("abc")
                }
            }
        }

        btnTakePicture.setOnClickListener {
            // Check for camera permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Request camera permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                // Permission already granted, launch the camera
                takePictureLauncher.launch(null)
            }
        }

        // Set up click listener for the back button
        backButton.setOnClickListener {
            // Navigate back to the main menu
            val intent = Intent(this, MainMenuActivity::class.java)
            startActivity(intent)
            finish() // Optional: close the current activity
        }
    }

    // Save the bitmap to a file and return the File object
    private fun saveBitmapToFile(context: Context, bitmap: Bitmap, capturedImage: String): File? {
        // Get the public external storage directory for pictures
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (imagesDir != null && !imagesDir.exists()) {
            imagesDir.mkdirs()
        }

        // Create a unique filename with a timestamp
        val timestamp = System.currentTimeMillis()
        val imageFile = File(imagesDir, "$capturedImage$timestamp.jpg")

        return try {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) // Compress to 90% quality
                out.flush()
            }
            // Notify the media scanner about the new file so that it is immediately available to the user
            MediaScannerConnection.scanFile(context, arrayOf(imageFile.toString()), null, null)
            // Return the File object
            imageFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadImageToIPFS(file: File) {
        if (!file.exists()) {
            runOnUiThread {
                Toast.makeText(this, "File does not exist: ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, requestBody)
            .build()


        val request = Request.Builder()
            .url("https://aqua-adjacent-kite-517.mypinata.cloud/pinning/pinFileToIPFS")
            .post(multipartBody)
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("pinata_api_key", "da8f6a2521c64aace556")
            .addHeader("pinata_secret_api_key", "fd9c150ad5db89f01b613c97138492de832245592d06dba18deb0f2ffbf52932")
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to upload image to IPFS", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val responseJson = JSONObject(response.body!!.string())
                    val ipfsHash = responseJson.getString("Hash")
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, "Image uploaded to IPFS: $ipfsHash", Toast.LENGTH_SHORT).show()
                    }
                    // Further actions if needed, such as navigating to the IPFS web interface
                } else {
                    // Handle unsuccessful response
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, "Failed to upload image to IPFSS", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    private fun sendIpfsUrlToServerForComparison(ipfsUrl: String): Float {
        var similarityRatio: Float = 0.0f
        val localImageName = "local_image_name.jpg" // Replace with the actual local image name
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("ipfsImageUrl", ipfsUrl)
            .addFormDataPart("localImageName", localImageName)
            .build()

        val request = Request.Builder()
            .url("http://171.247.145.173:5000")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Failed to compare images", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, "Failed to compare images", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val responseJson = JSONObject(response.body!!.string())
                    val similarity = responseJson.getDouble("similarity").toFloat()
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, "Image similarity: $similarity", Toast.LENGTH_SHORT).show()
                    }
                    similarityRatio = similarity
                }
            }
        })

        return similarityRatio
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, launch the camera
                    takePictureLauncher.launch(null)
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
