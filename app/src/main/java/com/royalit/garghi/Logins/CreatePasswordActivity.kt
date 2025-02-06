package com.royalit.garghi.Logins

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.royalit.garghi.AdaptersAndModels.CreatePasswordRequest
import com.royalit.garghi.AdaptersAndModels.CreatePasswordResponse
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityCreatePasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePasswordActivity : AppCompatActivity() {

    val binding: ActivityCreatePasswordBinding by lazy {
        ActivityCreatePasswordBinding.inflate(layoutInflater)
    }

    lateinit var email:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)
        email= intent.getStringExtra("email").toString()


        inits()


    }

    private fun inits() {
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            startActivity(Intent(this@CreatePasswordActivity, LoginActivity::class.java))
        }

        binding.cardLogin.setOnClickListener {
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                createApi()
            }
        }
    }

    private fun createApi() {
        val password_=binding.passwordEdit.text?.trim().toString()
        val cpassword_=binding.CpasswordEdit.text?.trim().toString()

        if(password_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Password")
            return
        }
        if(cpassword_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Conform Password")
            return
        }
        if(!password_.equals(cpassword_)){
            ViewController.showToast(applicationContext, "Enter password conform password not match")
            return
        }
            ViewController.showLoading(this@CreatePasswordActivity)
            val apiInterface = RetrofitClient.apiInterface
            val ceateP = CreatePasswordRequest(email, password_, cpassword_)

            apiInterface.createApi(ceateP).enqueue(object :
                Callback<CreatePasswordResponse> {
                override fun onResponse(call: Call<CreatePasswordResponse>, response: Response<CreatePasswordResponse>) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse.status.equals("success")) {
                            ViewController.showToast(applicationContext, "success")
                            startActivity(Intent(this@CreatePasswordActivity, LoginActivity::class.java))
                        } else {
                            ViewController.showToast(applicationContext, "Wrong Email Address")
                        }
                    } else {
                        ViewController.showToast(applicationContext, "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<CreatePasswordResponse>, t: Throwable) {
                    ViewController.hideLoading()
                    ViewController.showToast(applicationContext, "Try again: ${t.message}")
                }
            })

    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@CreatePasswordActivity, LoginActivity::class.java))
    }

}