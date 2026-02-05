package com.royalit.garghi.Logins

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.royalit.garghi.Activitys.DashBoardActivity
import com.royalit.garghi.AdaptersAndModels.EmailRequest
import com.royalit.garghi.AdaptersAndModels.ForgotEmailResponse
import com.royalit.garghi.AdaptersAndModels.OTPRequest
import com.royalit.garghi.AdaptersAndModels.OTPResponse
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityOtpactivityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPActivity : AppCompatActivity() {

    val binding: ActivityOtpactivityBinding by lazy {
        ActivityOtpactivityBinding.inflate(layoutInflater)
    }

    lateinit var email:String
    lateinit var type:String
    lateinit var token: String

    fun AppCompatEditText.showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        this.requestFocus()
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //FirebaseApp.initializeApp(this)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        email= intent.getStringExtra("email").toString()
        type= intent.getStringExtra("type").toString()


        inits()

    }


    private fun inits() {


        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_TOKEN_ERROR", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            token = task.result.toString()
            Log.e("FCM_TOKEN", "FCM Token: $token")
        })

        var count = 0
        fun setFocusable(){
            count++
            binding.pinEdit1.isFocusable = true
            binding.pinEdit1.isFocusableInTouchMode = true
            binding.pinEdit2.isFocusable = true
            binding.pinEdit2.isFocusableInTouchMode = true
            binding.pinEdit3.isFocusable = true
            binding.pinEdit3.isFocusableInTouchMode = true
            binding.pinEdit4.isFocusable = true
            binding.pinEdit4.isFocusableInTouchMode = true
        }
        binding.pinEdit1.setOnClickListener {
            if(count==0){
                setFocusable()
                binding.pinEdit1.requestFocus()
                binding.pinEdit1.showKeyboard()
            }
        }
        binding.pinEdit2.setOnClickListener {
            if(count==0){
                setFocusable()
                binding.pinEdit2.requestFocus()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.pinEdit2.showKeyboard()
                },100)

            }
        }
        binding.pinEdit3.setOnClickListener {
            if(count==0){
                setFocusable()
                binding.pinEdit3.requestFocus()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.pinEdit3.showKeyboard()
                },100)

            }
        }
        binding.pinEdit4.setOnClickListener {
            if(count==0){
                setFocusable()
                binding.pinEdit4.requestFocus()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.pinEdit4.showKeyboard()
                },100)

            }
        }


        binding.pinEdit1.addTextChangedListener {
            if (it?.toString()?.length == 1) binding.pinEdit2.requestFocus()
        }
        binding.pinEdit2.addTextChangedListener {
            if (it?.toString()?.length == 1) binding.pinEdit3.requestFocus() else binding.pinEdit1.requestFocus()
        }
        binding.pinEdit3.addTextChangedListener {
            if (it?.toString()?.length == 1) binding.pinEdit4.requestFocus() else binding.pinEdit2.requestFocus()
        }
        binding.pinEdit4.addTextChangedListener {
            if ((it?.toString()?.length?:0)<1) binding.pinEdit3.requestFocus()
        }


        binding.cardLogin.setOnClickListener {
            otpApi()
        }

        binding.txtResend.setOnClickListener {
            //resend otp
            forgotApi()
        }




    }

    private fun otpApi() {

        val pin1 = binding.pinEdit1.editableText.trim().toString()
        val pin2 = binding.pinEdit2.editableText.trim().toString()
        val pin3 = binding.pinEdit3.editableText.trim().toString()
        val pin4 = binding.pinEdit4.editableText.trim().toString()


        if(validateOtp()){
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
                return
            }else{
                ViewController.showLoading(this@OTPActivity)

                val apiInterface = RetrofitClient.apiInterface
                val loginRequest = OTPRequest(email, "$pin1$pin2$pin3$pin4",token)

                apiInterface.otpApi(loginRequest).enqueue(object : Callback<OTPResponse> {
                    override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                        ViewController.hideLoading()
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null && loginResponse.status.equals("success")) {
                                if (type.equals("Login")){
                                    startActivity(Intent(this@OTPActivity, DashBoardActivity::class.java))
                                }else{
                                    startActivity(Intent(this@OTPActivity,CreatePasswordActivity::class.java).apply {
                                        putExtra("email",email)
                                    })
                                    finish()
                                }

                            } else {
                                ViewController.showToast(applicationContext, "Otp Failed")
                            }
                        } else {
                            ViewController.showToast(applicationContext, "Error: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                        ViewController.hideLoading()
                        ViewController.showToast(applicationContext, "Try again: ${t.message}")
                    }
                })
            }
        }else{
            ViewController.showToast(applicationContext, "enter valid otp")
        }

    }

    private fun validateOtp():Boolean{
        val pin1 = binding.pinEdit1.editableText.trim().toString()
        val pin2 = binding.pinEdit2.editableText.trim().toString()
        val pin3 = binding.pinEdit3.editableText.trim().toString()
        val pin4 = binding.pinEdit4.editableText.trim().toString()
        return  pin1.isNotEmpty() && pin2.isNotEmpty() && pin3.isNotEmpty() && pin4.isNotEmpty()
    }


    //resend otp
    private fun forgotApi() {
        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
            return
        }else{
            ViewController.showLoading(this@OTPActivity)

            val apiInterface = RetrofitClient.apiInterface
            val forgotRequest = EmailRequest(email)

            apiInterface.forgotEmailApi(forgotRequest).enqueue(object : Callback<ForgotEmailResponse> {
                override fun onResponse(call: Call<ForgotEmailResponse>, response: Response<ForgotEmailResponse>) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse.status.equals("success")) {
                            ViewController.showToast(applicationContext, "Success")
                        } else {
                            ViewController.showToast(applicationContext, "Otp Sending failed")
                        }
                    } else {
                        ViewController.showToast(applicationContext, "Error: ${response.code()}")
                    }
                }
                override fun onFailure(call: Call<ForgotEmailResponse>, t: Throwable) {
                    ViewController.hideLoading()
                    ViewController.showToast(applicationContext, "Try again: ${t.message}")
                }
            })
        }
    }

}