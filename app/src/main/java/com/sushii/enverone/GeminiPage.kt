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

class GeminiPage : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var capturedImageView: ImageView
    private lateinit var responseTextView: TextView
    private lateinit var cameraButton: Button
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gemini_page)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        capturedImageView = findViewById(R.id.capturedImageView)
        responseTextView = findViewById(R.id.responseTextView)
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
                        "app is about best ways of garbage disposal and 3Rs of waste management.\n" +
                        "the only input you will have is in form of image taken from user's camera.\n" +
                        "input will only contain image of things the user thinks is trash and/or garbage and/or no longer useful for him/her.\n" +
                        "the output you generate will be inform of text prompt and must follow the following instructions and the sequence of your response should follow the same order-\n" +
                        "1  you will identify the type of garbage or object and material it is made out of or consists of and tell user about it.\n" +
                        "2 you will tell about the easiest way and most sustainable way to dispose the item so that it harms or impacts the environment in the most minimal way.\n" +
                        "3 if you think the  object in the input can be reused in any sort of manner then tell the user about it.\n" +
                        "4 if you identify the object in the input to be a living thing or a human being then you will make a passive aggressive but polite joke on user so that user feels guilty about thinking that a living thing is garbage.\n" +
                        "5 if you are not able to identify the objects present in the image due to any reason or if the picture is blur then make make a request to the user to retake the picture or take a clearer picture of the targeted object or garbage.\n")
                image(photo)
            }
            MainScope().launch {
                responseTextView.text = "Generating Response..."
                val response = generativeModel.generateContent(inputContent)

                responseTextView.text = response.text
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