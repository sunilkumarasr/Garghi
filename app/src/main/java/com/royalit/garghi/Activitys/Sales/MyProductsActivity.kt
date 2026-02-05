package com.royalit.garghi.Activitys.Sales

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.Activitys.EnquiryProductActivity
import com.royalit.garghi.AdaptersAndModels.MyProductsListAdapter
import com.royalit.garghi.AdaptersAndModels.MyProductsModel
import com.royalit.garghi.AdaptersAndModels.ProductItemDeleteModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityProductListingsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProductsActivity : AppCompatActivity(), View.OnClickListener   {

    val binding: ActivityProductListingsBinding by lazy {
        ActivityProductListingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        inits()

    }


    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "My Products"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(this@MyProductsActivity)){
            ViewController.showToast(this@MyProductsActivity, "Please check your connection ")
            return
        }else {
            MyProductsListApi()
        }

        binding.linearAddProducts.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.linearAddProducts -> {
                startActivity(Intent(this@MyProductsActivity, AddProductActivity::class.java))
            }

        }
    }


    private fun MyProductsListApi() {

        val userId = Preferences.loadStringValue(this@MyProductsActivity, Preferences.userId, "")
        Log.e("userId_",userId.toString())

        ViewController.showLoading(this@MyProductsActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.MyProductsListApi(userId).enqueue(object : retrofit2.Callback<List<MyProductsModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<MyProductsModel>>,
                response: retrofit2.Response<List<MyProductsModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        MyProductsDataSet(rsp)
                    } else {
                        binding.recyclerview.visibility = View.GONE
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                } else {
                    binding.recyclerview.visibility = View.GONE
                    binding.txtNoData.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: retrofit2.Call<List<MyProductsModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                binding.recyclerview.visibility = View.GONE
                binding.txtNoData.visibility = View.VISIBLE
            }
        })

    }
    private fun MyProductsDataSet(myProductsModellist: List<MyProductsModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(this@MyProductsActivity)
        binding.recyclerview.adapter = MyProductsListAdapter(this@MyProductsActivity, myProductsModellist) { item , type->
            if (type != null) {
                if(type.equals("Enquiry")){
                    startActivity(Intent(this@MyProductsActivity, EnquiryProductActivity::class.java).apply {
                        putExtra("post_id",item.id)
                        putExtra("post_Name",item.product)
                    })
                }
                if(type.equals("View")){
                    startActivity(Intent(this@MyProductsActivity, ProductDetaisActivity::class.java).apply {
                        putExtra("product_id",item.id)
                        putExtra("product_Name",item.product)
                    })
                }
                if(type.equals("Edit")){
                    startActivity(Intent(this@MyProductsActivity, EditProductActivity::class.java).apply {
                        putExtra("product_id",item.id)
                    })
                }
                if(type.equals("Delete")){
                    deleteDialog(item.id)
                }
            }
        }
    }


    private fun deleteDialog(id: String) {
        val dialogBuilder = AlertDialog.Builder(this@MyProductsActivity)
        dialogBuilder.setTitle("Delete")
        dialogBuilder.setMessage("Are you sure want to delete this item?")
        dialogBuilder.setPositiveButton("Yes") { dialog, whichButton ->
            deletePostApi(id)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("No") { dialog, whichButton ->
            dialog.dismiss()
        }
        val b = dialogBuilder.create()
        b.show()
    }
    private fun deletePostApi(id: String) {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.deleteProductApi(id).enqueue(object :
            Callback<ProductItemDeleteModel> {
            override fun onResponse(call: Call<ProductItemDeleteModel>, response: Response<ProductItemDeleteModel>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    if (deleteResponse != null && deleteResponse.message.equals("Product deleted successfully")) {
                        if(!ViewController.noInterNetConnectivity(this@MyProductsActivity)){
                            ViewController.showToast(this@MyProductsActivity, "Please check your connection ")
                            return
                        }else {
                            MyProductsListApi()
                        }
                    }
                } else {
                    ViewController.showToast(applicationContext, "Try again")
                }
            }
            override fun onFailure(call: Call<ProductItemDeleteModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again")
                Log.e("Tryagain:_ ", t.message.toString())
            }
        })
    }

}