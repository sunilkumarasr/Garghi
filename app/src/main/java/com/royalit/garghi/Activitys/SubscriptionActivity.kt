package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.CreateSubscriptionOrderRequest
import com.royalit.garghi.AdaptersAndModels.CreateSubscriptionOrderResponse
import com.royalit.garghi.AdaptersAndModels.SubscriptionAdapter
import com.royalit.garghi.AdaptersAndModels.SubscriptionPlan
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivitySubscriptionBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.royalit.garghi.AdaptersAndModels.SaveSubscriptionOrderRequest
import com.royalit.garghi.AdaptersAndModels.SaveSubscriptionOrderResponse

class SubscriptionActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private val binding: ActivitySubscriptionBinding by lazy {
        ActivitySubscriptionBinding.inflate(layoutInflater)
    }

    var userSubscriptionId: String = ""

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

//            startActivity(Intent(this@SubscriptionActivity, SubscriptionDetailsActivity::class.java).apply {
//                putExtra("title",plan.title)
//                putExtra("price",plan.price)
//            })
            OrderIdApi(plan.price , plan.id)

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


    private fun OrderIdApi(price: String, id: String) {
        val userId = Preferences.loadStringValue(this@SubscriptionActivity, Preferences.userId, "")

        ViewController.showLoading(this@SubscriptionActivity)


        val apiInterface = RetrofitClient.apiInterface
        val createSubscriptionOrderRequest = CreateSubscriptionOrderRequest(userId.toString(), id)

        apiInterface.createSubscriptionOrder(createSubscriptionOrderRequest).enqueue(object : Callback<CreateSubscriptionOrderResponse> {
            override fun onResponse(
                call: Call<CreateSubscriptionOrderResponse>,
                response: Response<CreateSubscriptionOrderResponse>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status == "success") {
                        val orderId = loginResponse?.data?.orderId
                        val keyId = loginResponse?.data?.keyId
                        val amount = loginResponse?.data?.amount
                        val currency = loginResponse?.data?.currency
                        val userEmail = loginResponse?.data?.userEmail
                        val userPhone = loginResponse?.data?.userPhone
                        userSubscriptionId = loginResponse?.data?.userSubscriptionId.toString()

                        paymentGateWay(orderId, keyId, amount, currency, userEmail, userPhone)

//                        ViewController.showToast(applicationContext, loginResponse.message ?: "Success")
//                        startActivity(
//                            Intent(this@SubscriptionActivity, DashBoardActivity::class.java)
//                        )
                    } else {
                        ViewController.showToast(applicationContext, "Error: ${loginResponse?.message ?: "Unknown Error"}")
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CreateSubscriptionOrderResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })
    }

    private fun paymentGateWay(orderId: String?, keyId: String?, amount: Int?, currency: String?, userEmail: String?, userPhone: String?) {

        val name = Preferences.loadStringValue(this@SubscriptionActivity, Preferences.name, "")
        val checkout = Checkout()
        checkout.setKeyID(keyId)

        val options = JSONObject().apply {
            put("name", "The Look Wear")
            put("description", "Garghi")
            put("order_id", orderId)
            put("currency", currency ?: "INR")
            put("amount", amount)

            put("prefill", JSONObject().apply {
                put("name", name)
                put("email", userEmail)
                put("contact", userPhone)
            })
        }

        checkout.open(this, options)
    }

    override fun onPaymentSuccess(
        razorpayPaymentID: String?,
        paymentData: PaymentData?
    ) {
        Log.d("RAZORPAY", "Payment Success")
        Log.d("RAZORPAY", "Payment ID: $razorpayPaymentID")
        Log.d("RAZORPAY", "Order ID: ${paymentData?.orderId}")
        Log.d("RAZORPAY", "Signature: ${paymentData?.signature}")

        // Verify payment with your backend
        verifyPayment(
            razorpayPaymentID+"",
        )
    }


    override fun onPaymentError(
        code: Int,
        description: String?,
        paymentData: PaymentData?
    ) {
        Toast.makeText(
            this,
            description ?: "Payment failed",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun verifyPayment(razorpayPaymentID: String) {
        val userId = Preferences.loadStringValue(this@SubscriptionActivity, Preferences.userId, "")

        ViewController.showLoading(this@SubscriptionActivity)


        val apiInterface = RetrofitClient.apiInterface
        val saveSubscriptionOrderRequest = SaveSubscriptionOrderRequest(userId.toString(), userSubscriptionId, razorpayPaymentID)

        apiInterface.saveSubscriptionOrder(saveSubscriptionOrderRequest).enqueue(object : Callback<SaveSubscriptionOrderResponse> {
            override fun onResponse(
                call: Call<SaveSubscriptionOrderResponse>,
                response: Response<SaveSubscriptionOrderResponse>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status == "success") {
                        ViewController.showToast(applicationContext, loginResponse.message ?: "Success")
                        startActivity(
                            Intent(this@SubscriptionActivity, DashBoardActivity::class.java)
                        )
                    } else {
                        ViewController.showToast(applicationContext, "Error: ${loginResponse?.message ?: "Unknown Error"}")
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SaveSubscriptionOrderResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })
    }

}