package com.royalit.garghi.Activitys

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.databinding.ActivityTransactionHistoryBinding
import com.royalit.garghi.databinding.ActivityWalletBinding

class TransactionHistoryActivity : AppCompatActivity() {


    val binding: ActivityTransactionHistoryBinding by lazy {
        ActivityTransactionHistoryBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()


    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Transactions History"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            //aboutUsApi()
        }

    }


}