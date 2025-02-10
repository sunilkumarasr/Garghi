package com.royalit.garghi.Activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.royalit.garghi.Activitys.Categorys.AddPostActivity
import com.royalit.garghi.AdaptersAndModels.ProfileResponse
import com.royalit.garghi.AdaptersAndModels.SocialMediaModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.Fragments.CategoriesFragment
import com.royalit.garghi.Fragments.HomeFragment
import com.royalit.garghi.Fragments.MyServiceFragment
import com.royalit.garghi.Fragments.ProfileFragment
import com.royalit.garghi.Fragments.SaleFragment
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityDashBoardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashBoardActivity : AppCompatActivity()  {

    val binding: ActivityDashBoardBinding by lazy {
        ActivityDashBoardBinding.inflate(layoutInflater)
    }

    var WhatsAppID = "9441085061"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //login
        Preferences.saveStringValue(applicationContext, Preferences.LOGINCHECK, "Login")


        getProfileApi()
        socialMediaApi()


        val intent=getIntent().getStringExtra("isNotification")
        if(intent=="1")
        {
            startActivity(Intent(this@DashBoardActivity, NotificationActivity::class.java))
        }


        // Load default fragment
        loadFragment(HomeFragment())
        ViewController.changeStatusBarColor(
            this,
            ContextCompat.getColor(this, R.color.bottom_home),
            false
        )

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    ViewController.changeStatusBarColor(
                        this,
                        ContextCompat.getColor(this, R.color.bottom_home),
                        false
                    )
                }
                R.id.nav_search -> {
                    loadFragment(CategoriesFragment())
                    ViewController.changeStatusBarColor(
                        this,
                        ContextCompat.getColor(this, R.color.bottom_search),
                        false
                    )
                }
                R.id.nav_services -> {
                    loadFragment(MyServiceFragment())
                    ViewController.changeStatusBarColor(
                        this,
                        ContextCompat.getColor(this, R.color.bottom_myservice),
                        false
                    )
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    ViewController.changeStatusBarColor(
                        this,
                        ContextCompat.getColor(this, R.color.bottom_profile),
                        false
                    )
                }
            }
            true
        }

        binding.fab.setOnClickListener {
            // Handle Floating Action Button Click
            startActivity(Intent(this@DashBoardActivity, AddPostActivity::class.java))
        }

    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
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
                            if (item.name.equals("WhatsApp")) {
                                WhatsAppID = item.code

                            }
                        }
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<SocialMediaModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
            }
        })
    }
    private fun openWhatsAppPage() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=+"+WhatsAppID+"&text=Hi%20there"))
        startActivity(browserIntent)
    }

    private fun getProfileApi() {
        val userId = Preferences.loadStringValue(this@DashBoardActivity , Preferences.userId, "")
        Log.e("userId_",userId.toString())
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.getProfileApi(userId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {

                    }
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
            }
        })
    }


    override fun onBackPressed() {
        super.onBackPressed()
        exitDialog()
    }


    private fun exitDialog(){
        val dialogBuilder = AlertDialog.Builder(this@DashBoardActivity)
        dialogBuilder.setTitle("Exit")
        dialogBuilder.setMessage("Are you sure want to exit this app?")
        dialogBuilder.setPositiveButton("OK", { dialog, whichButton ->
            finishAffinity()
            dialog.dismiss()
        })
        dialogBuilder.setNegativeButton("Cancel", { dialog, whichButton ->
            dialog.dismiss()
        })
        val b = dialogBuilder.create()
        b.show()
    }


}