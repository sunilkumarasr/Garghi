package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.BlogAdapter
import com.royalit.garghi.AdaptersAndModels.BlogListModel
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityBlogBinding

class BlogActivity : AppCompatActivity() {

    val binding: ActivityBlogBinding by lazy {
        ActivityBlogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Blog"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if (!ViewController.noInterNetConnectivity(applicationContext)) {
            ViewController.showToast(applicationContext, "Please check your connection ")
        } else {
            blogListApi()
        }

    }

    private fun blogListApi() {
        ViewController.showLoading(this@BlogActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.blogListApi().enqueue(object : retrofit2.Callback<List<BlogListModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<BlogListModel>>,
                response: retrofit2.Response<List<BlogListModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        val FaqList = response.body()
                        if (FaqList != null) {
                            DataSet(FaqList)
                        }
                    } else {
                    }
                } else {
                    ViewController.showToast(
                        this@BlogActivity,
                        "Error: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<List<BlogListModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                ViewController.showToast(this@BlogActivity, "Try again: ${t.message}")
            }
        })

    }
    private fun DataSet(faqlist: List<BlogListModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(this@BlogActivity)
        binding.recyclerview.adapter = BlogAdapter(faqlist) { item ->
            startActivity(Intent(this@BlogActivity, BlogDetailsActivity::class.java).apply {
                putExtra("title",item.title)
                putExtra("short_description",item.short_description)
                putExtra("description",item.description)
                putExtra("image",item.image)
            })
        }
    }

}