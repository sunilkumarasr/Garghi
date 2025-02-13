package com.royalit.garghi.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.royalit.garghi.Activitys.Categorys.CategoriesBasedItemsListActivity
import com.royalit.garghi.Activitys.DashBoardActivity
import com.royalit.garghi.Activitys.JobAlerts.JobAlertDetailsActivity
import com.royalit.garghi.AdaptersAndModels.BannerAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesModel
import com.royalit.garghi.AdaptersAndModels.Home.HomeCategoriesAdapter
import com.royalit.garghi.AdaptersAndModels.Home.HomeBannersModel
import com.royalit.garghi.AdaptersAndModels.JobAlerts.JobAlertHomeAdapter
import com.royalit.garghi.AdaptersAndModels.JobAlerts.JobAlertModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    //banners
    val imageList = ArrayList<String>() // Create image list
    private lateinit var bannerAdapter: BannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    private fun init() {

        val name = Preferences.loadStringValue(requireActivity(), Preferences.name, "")
        binding.txtName.setText("Hello "+name)

        if (!ViewController.noInterNetConnectivity(requireActivity())) {
            ViewController.showToast(requireActivity(), "Please check your connection ")
            return
        } else {
            HomebannersApi()
            categoriesApi()
           // jobAlertApi()
        }
    }

    private fun HomebannersApi() {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.HomebannersApi().enqueue(object : retrofit2.Callback<List<HomeBannersModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<HomeBannersModel>>,
                response: retrofit2.Response<List<HomeBannersModel>>
            ) {
                if (response.isSuccessful) {
                    val banners = response.body() ?: emptyList()
                    BannerDataSet(banners)
                } else {
                    ViewController.showToast(requireActivity(), "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<HomeBannersModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.showToast(requireActivity(), "Try again: ${t.message}")
            }
        })
    }
    private fun BannerDataSet(banners: List<HomeBannersModel>) {
        val imageList = mutableListOf<String>() // List to hold image URLs

        // Clear imageList if it has previous data
        imageList.clear()

        // Iterate over the banners and add the image URLs to imageList
        banners.forEach { banner ->
            imageList.add(banner.image) // Add the image URL from each banner
        }

        // Set the adapter with the image URLs
        bannerAdapter = BannerAdapter(imageList)
        binding?.viewPagerBanner?.adapter = bannerAdapter

        // Auto-scroll setup
        autoScrollViewPager(imageList)
    }

    private fun autoScrollViewPager(imageListnew: MutableList<String>) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = binding!!.viewPagerBanner.currentItem
                val nextItem = if (currentItem == imageListnew.size - 1) 0 else currentItem + 1
                binding!!.viewPagerBanner.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // 3-second delay
            }
        }
        handler.postDelayed(runnable, 3000) // Initial delay before starting the auto-scroll
    }

    private fun categoriesApi() {
            ViewController.showLoading(requireActivity())
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
                            ViewController.showToast(requireActivity(), "Error: ${response.code()}")
                        }
                    }
                    override fun onFailure(
                        call: retrofit2.Call<List<CategoriesModel>>,
                        t: Throwable
                    ) {
                        Log.e("cat_error", t.message.toString())
                        ViewController.hideLoading()
                        ViewController.showToast(requireActivity(), "Try again: ${t.message}")
                    }
                })
    }
    private fun DataSet(categories: List<CategoriesModel>) {
        // Get the first 5 items from the categories list
        val limitedCategories = if (categories.size > 11) categories.subList(0, 11) else categories

        val layoutManager = GridLayoutManager(activity, 4)
        binding.recyclerview.layoutManager = layoutManager

        val adapter = HomeCategoriesAdapter(limitedCategories, { item ->
            // Handle normal item click
            startActivity(Intent(activity, CategoriesBasedItemsListActivity::class.java).apply {
                putExtra("category_id", item.category_id)
                putExtra("category_Name", item.category)
            })
        }, {
//            // Handle "more" click
//            startActivity(Intent(requireActivity(), CategotirsListActivity::class.java))
            (activity as? DashBoardActivity)?.loadFragment(CategoriesFragment())
        })

        binding.recyclerview.adapter = adapter
    }

    private fun jobAlertApi() {
            val apiInterface = RetrofitClient.apiInterface
            apiInterface.jobAlertApi().enqueue(object : retrofit2.Callback<List<JobAlertModel>> {
                override fun onResponse(
                    call: retrofit2.Call<List<JobAlertModel>>,
                    response: retrofit2.Response<List<JobAlertModel>>
                ) {
                    if (response.isSuccessful) {
                        val rsp = response.body()
                        if (rsp != null) {
                            val joblist = response.body()
                            if (joblist != null) {
                                JobDataSet(joblist)
                            }
                        }
                    } else {
                        ViewController.showToast(requireActivity(), "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<JobAlertModel>>, t: Throwable) {
                    Log.e("cat_error", t.message.toString())
                    ViewController.showToast(requireActivity(), "Try again: ${t.message}")
                }
            })
    }
    private fun JobDataSet(joblist: List<JobAlertModel>) {
        binding.recyclerviewjobAlert.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewjobAlert.adapter = JobAlertHomeAdapter(joblist) { item ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
            startActivity(Intent(activity, JobAlertDetailsActivity::class.java).apply {
                putExtra("title",item.title)
                putExtra("description",item.description)
                putExtra("post_date",item.post_date)
                putExtra("last_date",item.last_date)
            })
        }
    }

}