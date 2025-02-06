package com.royalit.garghi.Activitys

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.Faq.FaqAdapter
import com.royalit.garghi.AdaptersAndModels.Faq.FaqModel
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityFaqBinding

class FaqActivity : AppCompatActivity() {

    val binding: ActivityFaqBinding by lazy {
        ActivityFaqBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "FAQ"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }


        if(!ViewController.noInterNetConnectivity(this@FaqActivity)){
            ViewController.showToast(this@FaqActivity, "Please check your connection ")
            return
        }else {
            faqListApi()
        }

    }

    private fun faqListApi() {

            ViewController.showLoading(this@FaqActivity)
            val apiInterface = RetrofitClient.apiInterface
            apiInterface.faqListApi().enqueue(object : retrofit2.Callback<List<FaqModel>> {
                override fun onResponse(
                    call: retrofit2.Call<List<FaqModel>>,
                    response: retrofit2.Response<List<FaqModel>>
                ) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val rsp = response.body()
                        if (rsp != null) {
                            val FaqList = response.body()
                            if (FaqList != null) {
                                FaqDataSet(FaqList)
                            }
                        } else {

                        }
                    } else {
                        ViewController.showToast(
                            this@FaqActivity,
                            "Error: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<FaqModel>>, t: Throwable) {
                    Log.e("cat_error", t.message.toString())
                    ViewController.hideLoading()
                    ViewController.showToast(this@FaqActivity, "Try again: ${t.message}")
                }
            })

    }
    private fun FaqDataSet(faqlist: List<FaqModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(this@FaqActivity)
        binding.recyclerview.adapter = FaqAdapter(faqlist) { item ->

        }
    }


}