package com.royalit.garghi.Activitys

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.databinding.ActivityBlogDetailsBinding

class BlogDetailsActivity : AppCompatActivity() {

    val binding: ActivityBlogDetailsBinding by lazy {
        ActivityBlogDetailsBinding.inflate(layoutInflater)
    }

    lateinit var title: String
    lateinit var short_description: String
    lateinit var description: String
    lateinit var image: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        title = intent.getStringExtra("title").toString()
        short_description = intent.getStringExtra("short_description").toString()
        description = intent.getStringExtra("description").toString()
        image = intent.getStringExtra("image").toString()



        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Blog Details"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        binding.txtTitless.text = title
        binding.txtShortDec.text = short_description

        //html text
        val htmlContent = description
        binding.txtDec.text = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_LEGACY)

        Glide.with(binding.img).load(image.toString()).into(binding.img)


    }

}