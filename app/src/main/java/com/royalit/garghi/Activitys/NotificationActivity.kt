package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.Notifications.NotificationAdapter
import com.royalit.garghi.AdaptersAndModels.Notifications.NotificationModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {
    var isNotification=""
    val binding: ActivityNotificationBinding by lazy {
        ActivityNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        isNotification= intent.getStringExtra("isNotification").toString()
        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Notifications"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            if(isNotification.equals("1")){
                startActivity(Intent(applicationContext,DashBoardActivity::class.java))
            }

            finish()
        }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            NotificationsListApi()
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(isNotification.equals("1")){
                    startActivity(Intent(applicationContext, DashBoardActivity::class.java))
                }
                finish()
            }
        })
    }

    private fun NotificationsListApi() {
        val userId = Preferences.loadStringValue(this@NotificationActivity, Preferences.userId, "")

        ViewController.showLoading(this@NotificationActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.NotificationsListApi(userId).enqueue(object : retrofit2.Callback<List<NotificationModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<NotificationModel>>,
                response: retrofit2.Response<List<NotificationModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        val joblist = response.body()
                        if (joblist != null) {
                            NotificationDataSet(joblist)
                        }
                    }
                } else {
                    ViewController.showToast(
                        this@NotificationActivity,
                        "Error: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<List<NotificationModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                ViewController.showToast(this@NotificationActivity, "Try again: ${t.message}")
            }
        })
    }
    private fun NotificationDataSet(joblist: List<NotificationModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(this@NotificationActivity)
        binding.recyclerview.adapter = NotificationAdapter(joblist, onItemClick = {
            if(it.type.equals("ServiceEnquiry"))
            {
                startActivity(Intent(this@NotificationActivity, EnquiryPostActivity::class.java).apply {
                    putExtra("post_id",it.product_id)
                    putExtra("post_Name",it.product_name)
                })
            }else if(it.type.equals("ProductEnquiry"))
            {
                startActivity(Intent(this@NotificationActivity, EnquiryProductActivity::class.java).apply {
                    putExtra("post_id",it.product_id)
                    putExtra("post_Name",it.product_name)
                })
            }
        })
    }

}