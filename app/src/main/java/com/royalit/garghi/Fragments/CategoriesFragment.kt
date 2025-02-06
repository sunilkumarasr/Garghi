package com.royalit.garghi.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.Activitys.Categorys.AddPostActivity
import com.royalit.garghi.Activitys.Categorys.CategoriesBasedItemsListActivity
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesHomeAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesModel
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.FragmentCategoriesBinding

class CategoriesFragment : Fragment(), View.OnClickListener {


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
        binding.cardAdd.setOnClickListener(this)

        categoriesApi()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.cardAdd -> {
                startActivity(Intent(activity, AddPostActivity::class.java))
            }

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
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = CategoriesHomeAdapter(categories) { item ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, CategoriesBasedItemsListActivity::class.java).apply {
                putExtra("category_id",item.category_id)
                putExtra("category_Name",item.category)
            })
        }
    }

}