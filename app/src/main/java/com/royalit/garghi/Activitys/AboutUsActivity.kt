package com.royalit.garghi.Activitys

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.royalit.garghi.AdaptersAndModels.AboutusResponse
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityAboutUsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutUsActivity : AppCompatActivity() {

    val binding: ActivityAboutUsBinding by lazy {
        ActivityAboutUsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }


    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "About us"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            aboutUsApi()
        }

    }

    private fun aboutUsApi() {
        ViewController.showLoading(this@AboutUsActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.aboutusApi().enqueue(object : Callback<AboutusResponse> {
            override fun onResponse(call: Call<AboutusResponse>, response: Response<AboutusResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        binding.txtNote.text = Html.fromHtml(rsp.description, Html.FROM_HTML_MODE_LEGACY)
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<AboutusResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })
    }

}