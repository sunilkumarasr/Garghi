package com.royalit.garghi.Activitys

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.databinding.ActivitySubscriptionBinding
import com.royalit.garghi.databinding.ActivitySubscriptionDetailsBinding
import kotlin.getValue

class SubscriptionDetailsActivity : AppCompatActivity() {

    private val binding: ActivitySubscriptionDetailsBinding by lazy {
        ActivitySubscriptionDetailsBinding.inflate(layoutInflater)
    }

    lateinit var title:String
    lateinit var price:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.bottom_myservice),
            true
        )


        val root = binding.root
        val left = root.paddingLeft
        val top = root.paddingTop
        val right = root.paddingRight
        val bottom = root.paddingBottom


        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                left + bars.left,
                top + bars.top,
                right + bars.right,
                bottom + bars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(root)

        title= intent.getStringExtra("title").toString()
        price= intent.getStringExtra("price").toString()

        binding.txtPlanName.text =title
        binding.txtAmount.text ="₹"+price

        binding.imgPaymentQr.setImageResource(R.drawable.sub_payment)

        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnShareScreenshot.setOnClickListener {
            val phoneNumber = "918074222366"
            val message = "Hi, I have paid for my subscription. Please verify my payment."

            val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"

            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No app available to open WhatsApp", Toast.LENGTH_SHORT).show()
            }
        }

    }
}