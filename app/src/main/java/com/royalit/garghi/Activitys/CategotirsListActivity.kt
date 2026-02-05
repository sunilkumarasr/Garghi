package com.royalit.garghi.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide.init
import com.royalit.garghi.Activitys.Categorys.CategoriesBasedItemsListActivity
import com.royalit.garghi.Activitys.JobAlerts.JobAlertDetailsActivity
import com.royalit.garghi.AdaptersAndModels.CategoriesListAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesModel
import com.royalit.garghi.AdaptersAndModels.Home.HomeCategoriesAdapter
import com.royalit.garghi.AdaptersAndModels.JobAlerts.JobAlertHomeAdapter
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityCategotirsListBinding
import com.royalit.garghi.databinding.ActivityEnquiryProductBinding

class CategotirsListActivity : AppCompatActivity() {

    val binding: ActivityCategotirsListBinding by lazy {
        ActivityCategotirsListBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Categorie's"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener {
            finish()
        }

        if (!ViewController.noInterNetConnectivity(this@CategotirsListActivity)) {
            ViewController.showToast(this@CategotirsListActivity, "Please check your connection ")
            return
        } else {
            categoriesApi()

        }

    }

    private fun categoriesApi() {
        ViewController.showLoading(this@CategotirsListActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.categoriesApi()
            .enqueue(object : retrofit2.Callback<List<CategoriesModel>> {
                override fun onResponse(
                    call: retrofit2.Call<List<CategoriesModel>>,
                    response: retrofit2.Response<List<CategoriesModel>>
                ) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val rsp = response.body()
                        if (rsp != null) {
                            val categories = response.body()
                            if (categories != null) {
                                DataSet(categories)
                            }
                        }
                    } else {
                        ViewController.showToast(this@CategotirsListActivity, "Error: ${response.code()}")
                    }
                }
                override fun onFailure(
                    call: retrofit2.Call<List<CategoriesModel>>,
                    t: Throwable
                ) {
                    Log.e("cat_error", t.message.toString())
                    ViewController.hideLoading()
                    ViewController.showToast(this@CategotirsListActivity, "Try again: ${t.message}")
                }
            })
    }
    private fun DataSet(categories: List<CategoriesModel>) {

        val layoutManager = GridLayoutManager(this@CategotirsListActivity, 3)
        binding.recyclerview.layoutManager = layoutManager

        val adapter = CategoriesListAdapter(categories) { item ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@CategotirsListActivity, CategoriesBasedItemsListActivity::class.java).apply {
                putExtra("category_id", item.category_id)
                putExtra("category_Name", item.category)
            })
        }

        binding.recyclerview.adapter = adapter
    }

}