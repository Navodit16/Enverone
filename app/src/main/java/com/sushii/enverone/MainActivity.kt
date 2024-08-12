package com.sushii.enverone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.media.browse.MediaBrowser
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import android.widget.TextView
import android.view.View
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks
import com.sushii.enverone.databinding.ActivityMainBinding
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal




class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageView: ImageView
    private lateinit var helloWorldContainer: LinearLayout
    private lateinit var textView: TextView
    private val texts = arrayOf("Plastic Paradise:  Over 8 million tons of plastic end up in our oceans each year, harming marine life and entering the food chain . That's a garbage truck full of plastic every minute!",
        "Choking on Air: Air pollution is linked to 7 million premature deaths every year, with most occurring in low and middle-income countries . It doesn't just affect lungs - it can damage hearts, brains, and even unborn babies.",
        "Chemical Soup:  Industrial waste and agricultural runoff pollute our water sources. These chemicals can disrupt hormones, cause birth defects, and even some cancers.",
        "Empty Oceans:  Overfishing is leading to collapsing fish populations. We rely on healthy oceans for food security and a balanced marine ecosystem.",
        "Silent Spring:  Pesticide use is killing vital insect populations like bees and butterflies.  These creatures are essential for pollinating crops that feed the world.")
    private var currentIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val button: Button = findViewById(R.id.button)
        val button2: Button = findViewById(R.id.button2)
        imageView = findViewById(R.id.imageView)
        helloWorldContainer = findViewById(R.id.hello_world_container)
        textView = findViewById(R.id.fivelines)

        checkInternetConnection()

        button.setOnClickListener {
            opengeminipage()
        }
        button2.setOnClickListener{
            opengeminipage2()
        }

        showHelloWorld()
        gettingstarted()


    }


    private fun showHelloWorld() {
        helloWorldContainer.visibility = View.VISIBLE
        textView.visibility = View.GONE
        handler.postDelayed({
            fadeOutHelloWorld()
        }, 5000)
    }

    private fun fadeOutHelloWorld() {
        helloWorldContainer.animate()
            .alpha(0f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    helloWorldContainer.visibility = View.GONE
                    textView.visibility = View.VISIBLE
                    textView.alpha = 0f
                    updateText()
                    fadeInText()
                    startTextLoop()
                }
            })
    }

    private fun startTextLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                fadeOutText()
                handler.postDelayed(this, 5000)
            }
        }, 5000)
    }

    private fun fadeOutText() {
        textView.animate()
            .alpha(0f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    updateText()
                    fadeInText()
                }
            })
    }

    private fun fadeInText() {
        textView.animate()
            .alpha(1f)
            .setDuration(1000)
            .setListener(null)
    }

    private fun updateText() {
        textView.text = texts[currentIndex]
        currentIndex = (currentIndex + 1) % texts.size
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
    private fun gettingstarted() {
        val infoPageLink =  Link("Getting Started")
            .setBold(false)
            .setTextColor(Color.parseColor("#f2ddb6"))
            .setTextColorOfHighlightedLink(Color.parseColor("#ffffff"))
            .setHighlightAlpha(0.4f)
            .setUnderlined(true)
            .setOnClickListener {
                val intent = Intent(this, GettingStarted::class.java)
                startActivity(intent)
            }


        binding.tvLinkText.applyLinks(infoPageLink)

    }
private fun opengeminipage(){
    val intent = Intent(this, GeminiPage::class.java)
    startActivity(intent)
}

    private fun checkInternetConnection() {
        NoInternetDialogSignal.Builder(
            this,
            lifecycle
        ).apply {
            dialogProperties.apply {

                cancelable = false // Optional
                noInternetConnectionTitle = "No Internet" // Optional
                noInternetConnectionMessage =
                    "Check your Internet connection and try again." // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "Please turn on" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Mobile data" // Optional

                onAirplaneModeTitle = "No Internet" // Optional
                onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
                pleaseTurnOffText = "Please turn off" // Optional
                airplaneModeOffButtonText = "Airplane mode" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()
    }
    private fun opengeminipage2(){
        val intent2 = Intent(this, GeminiPage2::class.java)
        startActivity(intent2)
    }

}
