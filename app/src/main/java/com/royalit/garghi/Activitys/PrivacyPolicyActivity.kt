package com.royalit.garghi.Activitys

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.royalit.garghi.AdaptersAndModels.PrivacyPolicyResponse
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityPrivacyPolicyBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrivacyPolicyActivity : AppCompatActivity() {

    val binding: ActivityPrivacyPolicyBinding by lazy {
        ActivityPrivacyPolicyBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }


    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Privacy Policy"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }


        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            privacyPolicyApi()
        }
    }

    private fun privacyPolicyApi() {
        ViewController.showLoading(this@PrivacyPolicyActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.privacyPolicyApi().enqueue(object : Callback<PrivacyPolicyResponse> {
            override fun onResponse(call: Call<PrivacyPolicyResponse>, response: Response<PrivacyPolicyResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        binding.txtNote.text = Html.fromHtml(loginResponse.description, Html.FROM_HTML_MODE_LEGACY)
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<PrivacyPolicyResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })
    }

}