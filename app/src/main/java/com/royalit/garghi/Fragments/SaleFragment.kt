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
import androidx.recyclerview.widget.GridLayoutManager
import com.denzcoskun.imageslider.models.SlideModel
import com.royalit.garghi.Activitys.Sales.AddProductActivity
import com.royalit.garghi.Activitys.Sales.ProductDetaisActivity
import com.royalit.garghi.AdaptersAndModels.SalesBannersModel
import com.royalit.garghi.AdaptersAndModels.SalesHome.ProductData
import com.royalit.garghi.AdaptersAndModels.SalesHome.SaleAdapter
import com.royalit.garghi.AdaptersAndModels.SalesHome.SaleModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.FragmentSaleBinding


class SaleFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSaleBinding


    private lateinit var SaleAdapter: SaleAdapter
    private var saleList = ArrayList<ProductData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSaleBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.cardAdd.setOnClickListener(this)

        SalesbannersApi()
        saleApi()

        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.cardAdd -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
            }

        }
    }

    private fun SalesbannersApi() {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.SalesbannersApi().enqueue(object : retrofit2.Callback<List<SalesBannersModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<SalesBannersModel>>,
                response: retrofit2.Response<List<SalesBannersModel>>
            ) {
                if (response.isSuccessful) {
                    val banners = response.body() ?: emptyList()
                    BannerDataSet(banners)
                } else {
                    ViewController.showToast(requireActivity(), "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<SalesBannersModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.showToast(requireActivity(), "Try again: ${t.message}")
            }
        })
    }
    private fun BannerDataSet(banners: List<SalesBannersModel>) {
        val imageList = mutableListOf<SlideModel>()
        banners.forEach {
            val imageUrl = it.image
            if (imageUrl.isNotEmpty()) {
                imageList.add(SlideModel(imageUrl))
            } else {
                imageList.add(
                    SlideModel(
                        R.drawable.home_bannes
                    )
                )
            }
        }
        binding.imageSlider.setImageList(imageList)
    }


    private fun saleApi() {
        val locationi = Preferences.loadStringValue(requireActivity(), Preferences.location, "")

        ViewController.showLoading(requireActivity())
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.saleApi(locationi).enqueue(object : retrofit2.Callback<SaleModel>{
            override fun onResponse(
                call: retrofit2.Call<SaleModel>,
                response: retrofit2.Response<SaleModel>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        saleList.clear()
                        saleList.addAll(rsp.data)
                        DataSet(saleList) // Pass the list of ProductData
                    } else {
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                } else {
                    binding.txtNoData.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: retrofit2.Call<SaleModel>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                binding.txtNoData.visibility = View.VISIBLE
                ViewController.showToast(requireActivity(), "Try again: ${t.message}")
            }
        })

    }
    private fun DataSet(sale: List<ProductData>) {
        val layoutManager = GridLayoutManager(activity, 2) // 3 columns in the grid
        binding.recyclerview.layoutManager = layoutManager
        SaleAdapter = SaleAdapter(sale) { item ->
            // Handle item click
            startActivity(Intent(activity, ProductDetaisActivity::class.java).apply {
                putExtra("product_id",item.product.id)
                putExtra("product_Name",item.product.product)
            })
        }
        binding.recyclerview.adapter = SaleAdapter
    }

    //search
    private fun filter(text: String) {
        val filteredList = saleList.filter { item ->
            item.product.product.contains(text, ignoreCase = true) ||
            item.product.address.contains(text, ignoreCase = true)
        }

        if (filteredList.isEmpty()) {
            binding.txtNoData.visibility = View.VISIBLE // Show "No Data" text
        } else {
            binding.txtNoData.visibility = View.GONE // Hide "No Data" text if items are found
        }

        // Update list only if catAdapter is initialized
        if (::SaleAdapter.isInitialized) {
            SaleAdapter.updateList(filteredList)
        }
    }


}