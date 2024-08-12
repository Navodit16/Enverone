package com.sushii.enverone

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.Window
import android.view.WindowManager
import android.os.Build
import com.google.ai.client.generativeai.GenerativeModel
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GeminiPage2 : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var capturedImageView: ImageView
    private lateinit var responseTextView2: TextView
    private lateinit var cameraButton: Button
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gemini_page2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        capturedImageView = findViewById(R.id.capturedImageView)
        responseTextView2 = findViewById(R.id.responseTextView2)
        cameraButton = findViewById(R.id.cameraButton)

        cameraButton.setOnClickListener {

            checkCameraPermission()
        }

        back()
        changeStatusBarColor("#6750a4")
    }
    private fun changeStatusBarColor(color: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = android.graphics.Color.parseColor(color)
        }
    }



    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val generativeModel = GenerativeModel(

            modelName = "gemini-1.5-flash",

            apiKey = BuildConfig.apiKey
        )
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            capturedImageView.setImageBitmap(photo)
            val inputContent = content {
                text("you are a chat model named SAM which will be used in a chat app built in android studio.\n" +
                        "you are an environment expert who specializes in information about plants and soils and what is best for them.\n" +
                        "the only input you will have is in form of image taken from user's camera.\n" +
                        "if you see soil in the input image then tell user best type of plants to grow on that soil,  where to get the plants and the best and easiest way to further fertilize the soil and also if composting is possible in that soil or not and if possible then steps to do it.\n" +
                        "if you could not detect soil or plants in given image then tell user about it and tell him/her to canpture a clear picture of soil or plants he/she wants information about.\n" +
                        "the output you generate will be inform of text prompt and must follow the following instructions and the sequence of your response should follow the same order-\n" +
                        "1  you will identify the type of plant or soil it is made out of or consists of and tell user about it.\n" +
                        "2 you will give your input about the soil or plant or both.\n" +
                        "5 if you are not able to identify the objects present in the image due to any reason or if the picture is blur then make make a request to the user to retake the picture or take a clearer picture of the targeted object.\n")
                image(photo)
            }
            MainScope().launch {
                responseTextView2.text = "Generating Response..."
                val response = generativeModel.generateContent(inputContent)
                responseTextView2.text = response.text
            }
        }



    }
    private fun back(){
        backButton = findViewById(R.id.imageButton4)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }


}