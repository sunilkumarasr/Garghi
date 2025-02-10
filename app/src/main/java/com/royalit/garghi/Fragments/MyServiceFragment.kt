package com.royalit.garghi.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.Activitys.Categorys.CategoriesBasedItemsListActivity
import com.royalit.garghi.Activitys.Categorys.EditPostActivity
import com.royalit.garghi.Activitys.Categorys.PostCategoriesDetailsActivity
import com.royalit.garghi.Activitys.EnquiryPostActivity
import com.royalit.garghi.AdaptersAndModels.CategoriesListAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesModel
import com.royalit.garghi.AdaptersAndModels.MyPostsList.MyPostListAdapter
import com.royalit.garghi.AdaptersAndModels.MyPostsList.MyPostsModel
import com.royalit.garghi.AdaptersAndModels.PostItemDeleteModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.FragmentHomeBinding
import com.royalit.garghi.databinding.FragmentMyServiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MyServiceFragment : Fragment(){

    private lateinit var binding: FragmentMyServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyServiceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }


    private fun init() {
        if(!ViewController.noInterNetConnectivity(requireActivity())){
            ViewController.showToast(requireActivity(), "Please check your connection ")
            return
        }else {
            MyPostsListApi()
        }

    }


    private fun MyPostsListApi() {

        val userId = Preferences.loadStringValue(requireActivity(), Preferences.userId, "")
        Log.e("userId_",userId.toString())

        ViewController.showLoading(requireActivity())
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
        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerview.adapter = MyPostListAdapter(joblist) { item , type->
            if (type != null) {

                if(type.equals("Enquiry")){
                    startActivity(Intent(requireActivity(), EnquiryPostActivity::class.java).apply {
                        putExtra("post_id",item.id)
                        putExtra("post_Name",item.title)
                    })
                }
                if(type.equals("View")){
                    startActivity(Intent(requireActivity(), PostCategoriesDetailsActivity::class.java).apply {
                        putExtra("category_id",item.category_id)
                        putExtra("post_id",item.id)
                        putExtra("post_Name",item.title)
                    })
                }
                if(type.equals("Edit")){
                    startActivity(Intent(requireActivity(), EditPostActivity::class.java).apply {
                        putExtra("post_id",item.id)
                    })
                }
                if(type.equals("Delete")){
                    deleteDialog(item.id)
                }
            }
        }



        // Set up the EditText listener to filter categories
        val editTextSearch = binding.editSearch // Assuming you're using view binding
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchQuery = s.toString().lowercase(Locale.getDefault()) // Get the query in lowercase
                val filteredList = joblist.filter {
                    it.title.lowercase(Locale.getDefault()).contains(searchQuery) // Filter categories
                }
                updateRecyclerView(filteredList)
            }
        })

    }
    private fun updateRecyclerView(filteredList: List<MyPostsModel>) {
        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerview.adapter = MyPostListAdapter(filteredList) { item , type->
            if (type != null) {

                if(type.equals("Enquiry")){
                    startActivity(Intent(requireActivity(), EnquiryPostActivity::class.java).apply {
                        putExtra("post_id",item.id)
                        putExtra("post_Name",item.title)
                    })
                }
                if(type.equals("View")){
                    startActivity(Intent(requireActivity(), PostCategoriesDetailsActivity::class.java).apply {
                        putExtra("category_id",item.category_id)
                        putExtra("post_id",item.id)
                        putExtra("post_Name",item.title)
                    })
                }
                if(type.equals("Edit")){
                    startActivity(Intent(requireActivity(), EditPostActivity::class.java).apply {
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
        val dialogBuilder = AlertDialog.Builder(requireActivity())
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
                        if(!ViewController.noInterNetConnectivity(requireActivity())){
                            ViewController.showToast(requireActivity(), "Please check your connection ")
                            return
                        }else {
                            MyPostsListApi()
                        }
                    }
                } else {
                    ViewController.showToast(requireActivity(), "Try again")
                }
            }
            override fun onFailure(call: Call<PostItemDeleteModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(requireActivity(), "Try again")
                Log.e("Tryagain:_ ", t.message.toString())
            }
        })
    }

}