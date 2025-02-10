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
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.Activitys.Categorys.AddPostActivity
import com.royalit.garghi.Activitys.Categorys.CategoriesBasedItemsListActivity
import com.royalit.garghi.Activitys.DashBoardActivity
import com.royalit.garghi.AdaptersAndModels.CategoriesListAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesHomeAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesModel
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.FragmentCategoriesBinding
import java.util.Locale

class CategoriesFragment : Fragment() {


    private lateinit var binding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {
        if (!ViewController.noInterNetConnectivity(requireActivity())) {
            ViewController.showToast(requireActivity(), "Please check your connection ")
            return
        } else {
            categoriesApi()
        }


    }


    private fun categoriesApi() {
        ViewController.showLoading(requireActivity())
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.categoriesApi().enqueue(object : retrofit2.Callback<List<CategoriesModel>> {
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
                    } else {

                    }
                } else {
                    ViewController.showToast(requireActivity(), "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<CategoriesModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                ViewController.showToast(requireActivity(), "Try again: ${t.message}")
            }
        })

    }

    private fun DataSet(categories: List<CategoriesModel>) {

        val layoutManager = GridLayoutManager(requireActivity(), 4)
        binding.recyclerview.layoutManager = layoutManager
        val adapter = CategoriesListAdapter(categories) { item ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(), CategoriesBasedItemsListActivity::class.java).apply {
                putExtra("category_id", item.category_id)
                putExtra("category_Name", item.category)
            })
        }
        binding.recyclerview.adapter = adapter


        // Set up the EditText listener to filter categories
        val editTextSearch = binding.editSearch // Assuming you're using view binding
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searchQuery = s.toString().lowercase(Locale.getDefault()) // Get the query in lowercase
                val filteredList = categories.filter {
                    it.category.lowercase(Locale.getDefault()).contains(searchQuery) // Filter categories
                }
                updateRecyclerView(filteredList)
            }
        })
    }

    private fun updateRecyclerView(filteredList: List<CategoriesModel>) {
        // Update the RecyclerView adapter with the filtered list
        val filteredAdapter = CategoriesListAdapter(filteredList) { item ->
            startActivity(Intent(requireActivity(), CategoriesBasedItemsListActivity::class.java).apply {
                putExtra("category_id", item.category_id)
                putExtra("category_Name", item.category)
            })
        }
        binding.recyclerview.adapter = filteredAdapter
    }

}