package com.royalit.garghi.Activitys.Categorys

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.royalit.garghi.AdaptersAndModels.Categorys.ZoomImageView
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient

class PostCategoriesZoomImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_categories_zoom_image)

        val root = findViewById<View>(R.id.zoomRoot)
        val btnClose = findViewById<TextView>(R.id.btnClose)
        val imgZoom = findViewById<ZoomImageView>(R.id.imgZoom)

        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.bottom_myservice),
            false
        )

        val left = root.paddingLeft
        val top = root.paddingTop
        val right = root.paddingRight
        val bottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            view.setPadding(
                left + bars.left,
                top + bars.top,
                right + bars.right,
                bottom + bars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(root)

        btnClose.setOnClickListener {
            finish()
        }

        val imagePath = intent.getStringExtra("imageURL")
            ?.trim()
            .orEmpty()

        if (imagePath.isBlank()) {
            Toast.makeText(
                this,
                "Image not available",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        val imageURL = if (
            imagePath.startsWith("https://", ignoreCase = true) ||
            imagePath.startsWith("http://", ignoreCase = true)
        ) {
            imagePath
        } else {
            RetrofitClient.Image_PathCat.trimEnd('/') +
                    "/" + imagePath.trimStart('/')
        }

        Glide.with(this)
            .load(imageURL)
            .fitCenter()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(imgZoom)
    }
}