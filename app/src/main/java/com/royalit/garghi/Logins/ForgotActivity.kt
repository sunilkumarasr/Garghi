package com.royalit.garghi.Logins

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royalit.garghi.AdaptersAndModels.EmailRequest
import com.royalit.garghi.AdaptersAndModels.ForgotEmailResponse
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityForgotBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotActivity : AppCompatActivity() {

    val binding: ActivityForgotBinding by lazy {
        ActivityForgotBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), true)

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

        inits()
    }


    private fun inits() {
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        binding.txtLogin.setOnClickListener {
            finish()
        }

        binding.cardForgot.setOnClickListener {
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                hideKeyboard()
                forgotApi()
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            binding.emailEdit.windowToken,
            0
        )

        binding.emailEdit.clearFocus()
    }

    private fun forgotApi() {
        val email=binding.emailEdit.text?.trim().toString()

        if(email.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Email")
            return
        }
        if (!validateEmail(email)) {
            ViewController.showToast(applicationContext, "Enter Valid email")
        }else{
            ViewController.showLoading(this@ForgotActivity)

            val apiInterface = RetrofitClient.apiInterface
            val forgotRequest = EmailRequest(email)

            apiInterface.forgotEmailApi(forgotRequest).enqueue(object : Callback<ForgotEmailResponse> {
                override fun onResponse(call: Call<ForgotEmailResponse>, response: Response<ForgotEmailResponse>) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse.status.equals("success")) {
                            startActivity(Intent(this@ForgotActivity,OTPActivity::class.java).apply {
                                putExtra("email",binding.emailEdit.editableText.trim().toString())
                                putExtra("type","Forgot")
                            })
                            finish()
                        } else {
                            ViewController.showToast(applicationContext, "Wrong Email Address")
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

    private fun validateEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
    }

}