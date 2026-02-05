package com.royalit.garghi.Activitys

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.databinding.ActivityUsefulLinksDetailsBinding

class UsefulLinksDetailsActivity : AppCompatActivity() {


    val binding: ActivityUsefulLinksDetailsBinding by lazy {
        ActivityUsefulLinksDetailsBinding.inflate(layoutInflater)
    }


    lateinit var url:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        url= intent.getStringExtra("url").toString()

        inits()
    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Useful Links Details"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        binding.webview.settings.setJavaScriptEnabled(true)

        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }
        binding.webview.loadUrl(url)

    }


}