package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.SubscriptionAdapter
import com.royalit.garghi.AdaptersAndModels.SubscriptionPlan
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.Logins.OTPActivity
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivitySubscriptionBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscriptionActivity : AppCompatActivity() {

    private val binding: ActivitySubscriptionBinding by lazy {
        ActivitySubscriptionBinding.inflate(layoutInflater)
    }

    private lateinit var subscriptionAdapter: SubscriptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.bottom_myservice),
            true
        )

        val root = binding.root
        val left = root.paddingLeft
        val top = root.paddingTop
        val right = root.paddingRight
        val bottom = root.paddingBottom


        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                left + bars.left,
                top + bars.top,
                right + bars.right,
                bottom + bars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(root)

        setupSubscriptionList()

        if (!ViewController.noInterNetConnectivity(applicationContext)) {
            ViewController.showToast(
                applicationContext,
                "Please check your connection"
            )
        } else {
            subscriptionApi()
        }

        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            finish()
        }
    }

    private fun setupSubscriptionList() {
        subscriptionAdapter = SubscriptionAdapter { plan ->
            Toast.makeText(
                this,
                "Selected: ${plan.title}",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(Intent(this@SubscriptionActivity, SubscriptionDetailsActivity::class.java).apply {
                putExtra("title",plan.title)
                putExtra("price",plan.price)
            })

        }

        binding.recyclerPlans.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlans.adapter = subscriptionAdapter
    }

    private fun subscriptionApi() {
        binding.progressBar.visibility = View.VISIBLE

        RetrofitClient.apiInterface.subscriptionApi()
            .enqueue(object : Callback<List<SubscriptionPlan>> {

                override fun onResponse(
                    call: Call<List<SubscriptionPlan>>,
                    response: Response<List<SubscriptionPlan>>
                ) {
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        subscriptionAdapter.submitList(response.body().orEmpty())
                    } else {
                        Toast.makeText(
                            this@SubscriptionActivity,
                            "Failed to load plans: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<List<SubscriptionPlan>>,
                    error: Throwable
                ) {
                    binding.progressBar.visibility = View.GONE

                    Toast.makeText(
                        this@SubscriptionActivity,
                        error.localizedMessage ?: "Unable to load plans",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}