package com.royalit.garghi.Activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.royalit.garghi.AdaptersAndModels.ContactUsResponse
import com.royalit.garghi.AdaptersAndModels.SocialMediaModel
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityContactUsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : AppCompatActivity() {

    val binding: ActivityContactUsBinding by lazy {
        ActivityContactUsBinding.inflate(layoutInflater)
    }


    var facebookPageID = ""
    var instagramPageID = ""
    var TwitterPageID = ""
    var WhatsAppID = "9441085061"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Contact Us"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            contactUsApi()
            socialMediaApi()
        }


        binding.txtFacebook.setOnClickListener {
            openFacebookPage()
        }
        binding.txtInstagram.setOnClickListener {
            openInstagramPage()
        }
        binding.txtTwitter.setOnClickListener {
            openTwitterPage()
        }
        binding.txtWhatsApp.setOnClickListener {
            openWhatsAppPage()
        }

    }

    private fun contactUsApi() {
        ViewController.showLoading(this@ContactUsActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.contactUsApi().enqueue(object : Callback<ContactUsResponse> {
            override fun onResponse(call: Call<ContactUsResponse>, response: Response<ContactUsResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        binding.txtPhone.text = rsp.phone
                        binding.txtEmail.text = rsp.email
                        binding.txtAddress.text = rsp.address
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ContactUsResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })
    }

    private fun socialMediaApi() {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.socialMediaApi().enqueue(object : retrofit2.Callback<List<SocialMediaModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<SocialMediaModel>>,
                response: retrofit2.Response<List<SocialMediaModel>>
            ) {
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null && rsp.isNotEmpty()) {
                        for (item in rsp) {
                            if (item.name.equals("instagram")) {
                                instagramPageID = item.code
                                binding.txtInstagram.visibility = View.VISIBLE
                            }
                            if (item.name.equals("Twitter")) {
                                TwitterPageID = item.code
                                binding.txtTwitter.visibility = View.VISIBLE
                            }
                            if (item.name.equals("facebook")) {
                                facebookPageID = item.code
                                binding.txtFacebook.visibility = View.VISIBLE
                            }
//                            if (item.name.equals("WhatsApp")) {
//                                WhatsAppID = item.code
//                                binding.txtWhatsApp.visibility = View.VISIBLE
//                            }
                        }
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<SocialMediaModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
            }
        })

    }

    private fun openFacebookPage() {
        val facebookUrl = "https://www.facebook.com/$facebookPageID"
        val facebookAppUrl = "fb://page/$facebookPageID"
        try {
            // Check if the Facebook app is installed
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookAppUrl))
            intent.setPackage("com.facebook.katana")
            startActivity(intent)
        } catch (e: Exception) {
            // If the Facebook app is not installed, open the page in the browser
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
            startActivity(intent)
        }
    }
    private fun openInstagramPage() {
        val instagramUrl = "https://www.instagram.com/"+instagramPageID
        val intent = Intent(Intent.ACTION_VIEW)
        try {
            packageManager.getPackageInfo("com.instagram.android", 0)
            intent.data = Uri.parse("http://instagram.com/_u/"+instagramPageID)
            intent.setPackage("com.instagram.android")
        } catch (e: Exception) {
            intent.data = Uri.parse(instagramUrl)
        }
        startActivity(intent)
    }
    private fun openTwitterPage() {
        val twitterUrl = "https://twitter.com/"+TwitterPageID
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl))
        startActivity(intent)
    }
    private fun openWhatsAppPage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+"+WhatsAppID+"&text=Hi%20there"))
        startActivity(browserIntent)
    }

}