package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royalit.garghi.Activitys.SplashActivity
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.Logins.LoginActivity
import com.royalit.garghi.R
import com.royalit.garghi.databinding.ActivityAboutUsBinding
import com.royalit.garghi.databinding.ActivityWalletBinding

class WalletActivity : AppCompatActivity() {

    val binding: ActivityWalletBinding by lazy {
        ActivityWalletBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary), false)

        inits()

    }

    private fun inits() {

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.cardWithdraw.setOnClickListener {
            startActivity(Intent(this@WalletActivity, WithdrawActivity::class.java))
        }
        binding.cardTransactions.setOnClickListener {
            startActivity(Intent(this@WalletActivity, TransactionHistoryActivity::class.java))
        }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            //WalletApi()
        }


    }

}