package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.EnquieryProductModel
import com.royalit.garghi.AdaptersAndModels.MyProductEnquieryAdapter
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityEnquiryProductBinding

class EnquiryProductActivity : AppCompatActivity() {

    val binding: ActivityEnquiryProductBinding by lazy {
        ActivityEnquiryProductBinding.inflate(layoutInflater)
    }

    lateinit var post_id: String
    lateinit var post_Name: String
    var isNotification=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)


        post_id = intent.getStringExtra("post_id").toString()
        post_Name = intent.getStringExtra("post_Name").toString()
        if(intent.hasExtra("isNotification"))
            isNotification=intent.getStringExtra("isNotification").toString()

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = post_Name
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            if(isNotification.equals("1"))
            {
                startActivity(Intent(applicationContext,DashBoardActivity::class.java))
            }
            finish()
        }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            EnquieryProductListApi()
        }
    }

    private fun EnquieryProductListApi() {

        ViewController.showLoading(this@EnquiryProductActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.EnquieryProductListApi(post_id).enqueue(object : retrofit2.Callback<List<EnquieryProductModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<EnquieryProductModel>>,
                response: retrofit2.Response<List<EnquieryProductModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        DataSet(rsp)
                    } else {
                        binding.recyclerview.visibility = View.GONE
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                } else {
                    binding.recyclerview.visibility = View.GONE
                    binding.txtNoData.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: retrofit2.Call<List<EnquieryProductModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                binding.recyclerview.visibility = View.GONE
                binding.txtNoData.visibility = View.VISIBLE
            }
        })

    }
    private fun DataSet(joblist: List<EnquieryProductModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(this@EnquiryProductActivity)
        binding.recyclerview.adapter = MyProductEnquieryAdapter(joblist) { item ->

        }
    }

}