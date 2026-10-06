package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.AdaptersAndModels.MySubscription
import com.royalit.garghi.AdaptersAndModels.MySubscriptionAdapter
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityMySubscriptionBinding
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySubscriptionActivity : AppCompatActivity() {

    private val binding: ActivityMySubscriptionBinding by lazy {
        ActivityMySubscriptionBinding.inflate(layoutInflater)
    }

    private lateinit var mySubscriptionAdapter: MySubscriptionAdapter

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

        mySubscriptionAdapter = MySubscriptionAdapter()

        binding.recyclerMySubscriptions.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerMySubscriptions.adapter =
            mySubscriptionAdapter

        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            finish()
        }

        binding.btnBuyPackage.setOnClickListener {
            startActivity(
                Intent(this, SubscriptionActivity::class.java)
            )
        }

        if (!ViewController.noInterNetConnectivity(applicationContext)) {
            ViewController.showToast(this, "Please check your connection")
        } else {
            mySubscriptionsApi()
        }
    }

    private fun mySubscriptionsApi() {
        val userId = Preferences.loadStringValue(this@MySubscriptionActivity, Preferences.userId, "")


        if (userId == null) {
            ViewController.showToast(this, "Please log in again")
            return
        }

        ViewController.showLoading(this)

        RetrofitClient.apiInterface.mySubscriptionsApi(userId.toString())
            .enqueue(object : Callback<JsonElement> {

                override fun onResponse(
                    call: Call<JsonElement>,
                    response: Response<JsonElement>
                ) {
                    ViewController.hideLoading()

                    if (!response.isSuccessful) {
                        ViewController.showToast(
                            this@MySubscriptionActivity,
                            "Error: ${response.code()}"
                        )
                        return
                    }

                    val json = response.body()
                    if (json == null) {
                        ViewController.showToast(
                            this@MySubscriptionActivity,
                            "Empty response"
                        )
                        return
                    }

                    Log.d("MySubscriptions", "Response: $json")

                    if (json.isJsonObject) {
                        val obj = json.asJsonObject
                        val message = obj.get("message")
                            ?.takeUnless { it.isJsonNull }
                            ?.asString

                        if (message.equals("No records found", ignoreCase = true)) {
                            mySubscriptionAdapter.submitList(emptyList())
                            binding.txtNoSubscriptions.visibility = View.VISIBLE
                            binding.txtPurchased.text = "0"
                            binding.txtUsed.text = "0"
                            binding.txtAvailable.text = "0"
                            return
                        }
                    }

                    try {
                        showSubscriptions(json)
                    } catch (e: Exception) {
                        Log.e("MySubscriptions", "Parsing failed: $json", e)
                        ViewController.showToast(
                            this@MySubscriptionActivity,
                            "Unable to read subscription data"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<JsonElement>,
                    t: Throwable
                ) {
                    ViewController.hideLoading()
                    Log.e("MySubscriptions", "Request failed", t)
                    ViewController.showToast(
                        this@MySubscriptionActivity,
                        "Request failed: ${t.message}"
                    )
                }
            })
    }

    private fun showSubscriptions(json: JsonElement) {
        if (!json.isJsonObject) {
            ViewController.showToast(this, "Unexpected subscription response")
            return
        }

        val root = json.asJsonObject

        val summary = root.get("summary")
            ?.takeIf { it.isJsonObject }
            ?.asJsonObject

        val subscriptionsJson = root.get("subscriptions")

        if (subscriptionsJson == null || !subscriptionsJson.isJsonArray) {
            val message = root.get("message")
                ?.takeIf { it.isJsonPrimitive }
                ?.asString

            if (message.equals("No records found", ignoreCase = true)) {
                displaySubscriptions(emptyList(), summary)
            } else {
                ViewController.showToast(
                    this,
                    message ?: "Unexpected subscription response"
                )
            }

            return
        }

        val listType = object :
            com.google.gson.reflect.TypeToken<List<MySubscription>>() {}.type

        val subscriptions: List<MySubscription> =
            com.google.gson.Gson().fromJson(subscriptionsJson, listType)

        displaySubscriptions(subscriptions, summary)
    }

    private fun displaySubscriptions(
        subscriptions: List<MySubscription>,
        totals: JsonObject?
    ) {
        mySubscriptionAdapter.submitList(subscriptions)

        binding.txtNoSubscriptions.visibility =
            if (subscriptions.isEmpty()) View.VISIBLE else View.GONE

        binding.recyclerMySubscriptions.visibility =
            if (subscriptions.isEmpty()) View.GONE else View.VISIBLE

        // Read overall totals from summary, not from an individual subscription.
        binding.txtPurchased.text =
            totals?.numberText("total_listings_purchased") ?: "0"

        binding.txtUsed.text =
            totals?.numberText("total_listings_used") ?: "0"

        binding.txtAvailable.text =
            totals?.numberText("total_listings_available") ?: "0"
    }


    private fun JsonObject.numberText(key: String): String? {
        val value = get(key) ?: return null
        return if (value.isJsonNull || !value.isJsonPrimitive) {
            null
        } else {
            value.asString
        }
    }
}