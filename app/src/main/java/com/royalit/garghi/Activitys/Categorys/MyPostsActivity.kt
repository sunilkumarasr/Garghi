package com.royalit.garghi.Activitys.Categorys

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
import com.royalit.garghi.Activitys.EnquiryPostActivity
import com.royalit.garghi.AdaptersAndModels.MyPostsList.MyPostListAdapter
import com.royalit.garghi.AdaptersAndModels.MyPostsList.MyPostsModel
import com.royalit.garghi.AdaptersAndModels.PostItemDeleteModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityPostListingsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPostsActivity : AppCompatActivity(),View.OnClickListener {

    val binding: ActivityPostListingsBinding by lazy {
        ActivityPostListingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "My Services"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(this@MyPostsActivity)){
            ViewController.showToast(this@MyPostsActivity, "Please check your connection ")
            return
        }else {
            MyPostsListApi()
        }

        binding.linearAddPost.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.linearAddPost -> {
                startActivity(Intent(this@MyPostsActivity, AddPostActivity::class.java))
            }
        }
    }


    private fun MyPostsListApi() {

        val userId = Preferences.loadStringValue(this@MyPostsActivity, Preferences.userId, "")
        Log.e("userId_",userId.toString())

        ViewController.showLoading(this@MyPostsActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.MyPostsListApi(userId).enqueue(object : retrofit2.Callback<List<MyPostsModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<MyPostsModel>>,
                response: retrofit2.Response<List<MyPostsModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        MyPostsDataSet(rsp)
                    } else {
                        binding.recyclerview.visibility = View.GONE
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                } else {
                    binding.recyclerview.visibility = View.GONE
                    binding.txtNoData.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: retrofit2.Call<List<MyPostsModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                binding.recyclerview.visibility = View.GONE
                binding.txtNoData.visibility = View.VISIBLE
            }
        })

    }
    private fun MyPostsDataSet(joblist: List<MyPostsModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(this@MyPostsActivity)
        binding.recyclerview.adapter = MyPostListAdapter(joblist) { item , type->
            if (type != null) {

                if(type.equals("Enquiry")){
                    startActivity(Intent(this@MyPostsActivity, EnquiryPostActivity::class.java).apply {
                        putExtra("post_id",item.id)
                        putExtra("post_Name",item.title)
                    })
                }
                if(type.equals("View")){
                    startActivity(Intent(this@MyPostsActivity, PostCategoriesDetailsActivity::class.java).apply {
                        putExtra("category_id",item.category_id)
                        putExtra("post_id",item.id)
                        putExtra("post_Name",item.title)
                    })
                }
                if(type.equals("Edit")){
                    startActivity(Intent(this@MyPostsActivity, EditPostActivity::class.java).apply {
                        putExtra("post_id",item.id)
                    })
                }
                if(type.equals("Delete")){
                    deleteDialog(item.id)
                }
            }
        }
    }

    private fun deleteDialog(id: String) {
        val dialogBuilder = AlertDialog.Builder(this@MyPostsActivity)
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
        apiInterface.deletePostApi(id).enqueue(object :
            Callback<PostItemDeleteModel> {
            override fun onResponse(call: Call<PostItemDeleteModel>, response: Response<PostItemDeleteModel>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    if (deleteResponse != null && deleteResponse.message.equals("Product deleted successfully")) {
                        if(!ViewController.noInterNetConnectivity(this@MyPostsActivity)){
                            ViewController.showToast(this@MyPostsActivity, "Please check your connection ")
                            return
                        }else {
                            MyPostsListApi()
                        }
                    }
                } else {
                    ViewController.showToast(applicationContext, "Try again")
                }
            }
            override fun onFailure(call: Call<PostItemDeleteModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again")
                Log.e("Tryagain:_ ", t.message.toString())
            }
        })
    }


}