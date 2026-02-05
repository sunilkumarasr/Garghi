package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.royalit.garghi.AdaptersAndModels.HelpAndSupport.HelpAndSupportRequest
import com.royalit.garghi.AdaptersAndModels.HelpAndSupport.HelpAndSupportResponse
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityHelpAndSupportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HelpAndSupportActivity : AppCompatActivity() {

    val binding: ActivityHelpAndSupportBinding by lazy {
        ActivityHelpAndSupportBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Help And Support"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        binding.cardLogin.setOnClickListener {
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                HelpAndSupportApi()
            }
        }

    }

    private fun HelpAndSupportApi() {
        val name=binding.nameEdit.text?.trim().toString()
        val email=binding.emailEdit.text?.trim().toString()
        val phone=binding.mobileEdit.text?.trim().toString()
        val message =binding.messageEdit.text?.trim().toString()

        if(name.isEmpty()){
            ViewController.showToast(applicationContext, "Enter name")
            return
        }
        if(email.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Email")
            return
        }
        if(phone.isEmpty()){
            ViewController.showToast(applicationContext, "Enter mobile number")
            return
        }
        if(message.isEmpty()){
            ViewController.showToast(applicationContext, "Enter message")
            return
        }

        if (!validateEmail(email)) {
            ViewController.showToast(applicationContext, "Enter Valid email")
            return
        }

        if (!validateMobileNumber(phone)) {
            ViewController.showToast(applicationContext, "Enter Valid mobile number")
            return
        }

        ViewController.showLoading(this@HelpAndSupportActivity)
        val apiInterface = RetrofitClient.apiInterface
        val helpsupportRequest = HelpAndSupportRequest(name,email,phone,message)

        apiInterface.HelpAndSupportApi(helpsupportRequest).enqueue(object : Callback<HelpAndSupportResponse> {
            override fun onResponse(call: Call<HelpAndSupportResponse>, response: Response<HelpAndSupportResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status.equals("success")) {
                        ViewController.showToast(applicationContext, "Success")
                        startActivity(Intent(this@HelpAndSupportActivity, DashBoardActivity::class.java))
                    } else {
                        ViewController.showToast(applicationContext, "Failed")
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<HelpAndSupportResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })

    }


    private fun validateEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
    }

    private fun validateMobileNumber(mobile: String): Boolean {
        val mobilePattern = "^[6-9][0-9]{9}\$"
        return Patterns.PHONE.matcher(mobile).matches() && mobile.matches(Regex(mobilePattern))
    }

}