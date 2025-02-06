package com.royalit.garghi.Activitys.JobAlerts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.JobAlerts.JobAlertHomeAdapter
import com.royalit.garghi.AdaptersAndModels.JobAlerts.JobAlertModel
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityJobAlertsBinding

class JobAlertsActivity : AppCompatActivity() {

    val binding: ActivityJobAlertsBinding by lazy {
        ActivityJobAlertsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Job Alerts"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(this@JobAlertsActivity)){
            ViewController.showToast(this@JobAlertsActivity, "Please check your connection ")
            return
        }else {
            jobAlertApi()
        }
    }

    private fun jobAlertApi() {

        ViewController.showLoading(this@JobAlertsActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.jobAlertApi().enqueue(object : retrofit2.Callback<List<JobAlertModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<JobAlertModel>>,
                response: retrofit2.Response<List<JobAlertModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        val joblist = response.body()
                        if (joblist != null) {
                            JobDataSet(joblist)
                        }
                    } else {

                    }
                } else {
                    ViewController.showToast(
                        this@JobAlertsActivity,
                        "Error: ${response.code()}"
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<List<JobAlertModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                ViewController.showToast(this@JobAlertsActivity, "Try again: ${t.message}")
            }
        })

    }
    private fun JobDataSet(joblist: List<JobAlertModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(this@JobAlertsActivity)
        binding.recyclerview.adapter = JobAlertHomeAdapter(joblist) { item ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@JobAlertsActivity, JobAlertDetailsActivity::class.java).apply {
                putExtra("title",item.title)
                putExtra("description",item.description)
                putExtra("post_date",item.post_date)
                putExtra("last_date",item.last_date)
            })
        }
    }

}