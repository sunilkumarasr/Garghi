package com.royalit.garghi.Activitys.Categorys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.royalit.garghi.AdaptersAndModels.Categorys.SubCategoriesModel
import com.royalit.garghi.AdaptersAndModels.PostBannersModel
import com.royalit.garghi.AdaptersAndModels.ProfileResponse
import com.royalit.garghi.AdaptersAndModels.SubCategories.SubCategoriesAdapter
import com.royalit.garghi.AdaptersAndModels.SubCategoriesItems.SubCategoriesItemsAdapter
import com.royalit.garghi.AdaptersAndModels.SubCategoriesItems.SubCategoriesItemsModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.LocationBottomSheetFragment
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityCategoriesBasedItemsListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoriesBasedItemsListActivity : AppCompatActivity() {

    val binding: ActivityCategoriesBasedItemsListBinding by lazy {
        ActivityCategoriesBasedItemsListBinding.inflate(layoutInflater)
    }

    lateinit var category_id:String
    lateinit var category_Name:String
    lateinit var sub_id: String

    private lateinit var catAdapter: SubCategoriesItemsAdapter
    private var categoriesList = ArrayList<SubCategoriesItemsModel>()

    var lat: String = ""
    var longi: String = ""
    var location: String = ""
    var Km: String = ""

    //call
    companion object {
        private const val REQUEST_CALL_PERMISSION = 1
    }
    var mobile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        category_id= intent.getStringExtra("category_id").toString()
        category_Name= intent.getStringExtra("category_Name").toString()

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = category_Name
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }


        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            PostBannersApi()
        }


        val lati = Preferences.loadStringValue(this@CategoriesBasedItemsListActivity, Preferences.lat, "")
        val longii = Preferences.loadStringValue(this@CategoriesBasedItemsListActivity, Preferences.longi, "")
        val locationi = Preferences.loadStringValue(this@CategoriesBasedItemsListActivity, Preferences.location, "")
        val klm = Preferences.loadStringValue(this@CategoriesBasedItemsListActivity, Preferences.km, "")
        if(lati.equals("") || longii.equals("") || klm.equals("")){
            getProfileApi()
        }else{
            binding.txtLocation.text = locationi
            lat = lati.toString()
            longi = longii.toString()
            Km = klm.toString()
            subcategoriesApi()
        }

        binding.imgLocationChange.setOnClickListener {
            bottomPopup()
        }

        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun getProfileApi() {
        val userId = Preferences.loadStringValue(this@CategoriesBasedItemsListActivity, Preferences.userId, "")
        Log.e("userId_",userId.toString())

        val apiInterface = RetrofitClient.apiInterface
        apiInterface.getProfileApi(userId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        binding.txtLocation.text = rsp.data?.location.toString()
                        lat = rsp.data?.latitude.toString()
                        longi = rsp.data?.longitude.toString()
                        Km = rsp.data?.longitude.toString()
                        subcategoriesApi()
                    }
                } else {
                    ViewController.showToast(this@CategoriesBasedItemsListActivity, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                ViewController.showToast(this@CategoriesBasedItemsListActivity, t.message.toString())
            }
        })
    }

    //banners
    private fun PostBannersApi() {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.PostBannersApi().enqueue(object : retrofit2.Callback<List<PostBannersModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<PostBannersModel>>,
                response: retrofit2.Response<List<PostBannersModel>>
            ) {
                if (response.isSuccessful) {
                    val banners = response.body() ?: emptyList()
                    BannerDataSet(banners)
                } else {
                    ViewController.showToast(this@CategoriesBasedItemsListActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<PostBannersModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.showToast(this@CategoriesBasedItemsListActivity, "Try again: ${t.message}")
            }
        })
    }
    private fun BannerDataSet(banners: List<PostBannersModel>) {
        val imageList = mutableListOf<SlideModel>()
        banners.forEach {
            val imageUrl = it.image
            if (imageUrl.isNotEmpty()) {
                imageList.add(SlideModel(imageUrl))
            } else {
                imageList.add(
                    SlideModel(
                        R.drawable.ic_launcher_background
                    )
                )
            }
        }
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
    }

    private fun subcategoriesApi() {
        binding.txtNoData.visibility = View.GONE
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.subcategoriesApi(category_id).enqueue(object : retrofit2.Callback<List<SubCategoriesModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<SubCategoriesModel>>,
                response: retrofit2.Response<List<SubCategoriesModel>>
            ) {
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        val categories = response.body()
                        if (categories != null) {
                            if (categories.size != 0) {
                                subcategoriesdataList(categories)
                            } else {
                                binding.txtNoData.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                } else {
                    ViewController.showToast(this@CategoriesBasedItemsListActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<SubCategoriesModel>>, t: Throwable) {
                Log.e("citys_error", t.message.toString())
                binding.txtNoData.visibility = View.VISIBLE

            }
        })

    }
    private fun subcategoriesdataList(categoriesl: List<SubCategoriesModel>) {

        // Initialize RecyclerView
        binding.Crecyclerview.layoutManager = LinearLayoutManager(this@CategoriesBasedItemsListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.Crecyclerview.adapter = SubCategoriesAdapter(categoriesl) { item ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
            sub_id = item.id
            categoriesBasedItemsApi(item.id)
        }

        //auto run for first item
        if (categoriesl.isNotEmpty()) {
            val firstSubcategory = categoriesl[0]
            sub_id = firstSubcategory.id
            categoriesBasedItemsApi(firstSubcategory.id)// Auto run for the first subcategory
        }

    }

    private fun categoriesBasedItemsApi(subId: String) {
        binding.txtNoData.visibility = View.GONE
        binding.recyclerview.visibility = View.VISIBLE
        ViewController.showLoading(this@CategoriesBasedItemsListActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.categoriesBasedItemsApi(category_id, subId, lat, longi, Km).enqueue(object : retrofit2.Callback<List<SubCategoriesItemsModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<SubCategoriesItemsModel>>,
                response: retrofit2.Response<List<SubCategoriesItemsModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        val categories = response.body()
                        if (categories != null) {
                            // Initialize and clear the list
                            categoriesList.clear()
                            categoriesList.addAll(categories)
                            if (categoriesList.size > 0) {
                                categoriesDataSet(categoriesList)
                            } else {
                                binding.recyclerview.visibility = View.GONE
                                binding.txtNoData.visibility = View.VISIBLE
                            }
                        } else {
                            binding.recyclerview.visibility = View.GONE
                            binding.txtNoData.visibility = View.VISIBLE
                        }
                    } else {
                        binding.recyclerview.visibility = View.GONE
                        binding.txtNoData.visibility = View.VISIBLE
                    }
                } else {
                    ViewController.showToast(this@CategoriesBasedItemsListActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<SubCategoriesItemsModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                ViewController.hideLoading()
                binding.recyclerview.visibility = View.GONE
                binding.txtNoData.visibility = View.VISIBLE
            }
        })

    }

    private fun categoriesDataSet(categories: List<SubCategoriesItemsModel>) {
        // Initialize RecyclerView
        binding.recyclerview.layoutManager = LinearLayoutManager(this@CategoriesBasedItemsListActivity)
        catAdapter = SubCategoriesItemsAdapter(categories) { item, type ->
            //Toast.makeText(activity, "Clicked: ${item.text}", Toast.LENGTH_SHORT).show()
            if (type.equals("call")){
                mobile = item.mobile
                checkAndRequestPermission()
            }else{
                startActivity(Intent(this@CategoriesBasedItemsListActivity, PostCategoriesDetailsActivity::class.java).apply {
                    putExtra("category_id",category_id)
                    putExtra("sub_id",sub_id)
                    putExtra("post_id",item.id)
                    putExtra("post_Name",item.title)
                })
            }
        }
        binding.recyclerview.adapter = catAdapter
    }

    //search
    private fun filter(text: String) {
        val filteredList = categoriesList.filter { item ->
            item.services.contains(text, ignoreCase = true)
        }

        if (filteredList.isEmpty()) {
            binding.txtNoData.visibility = View.VISIBLE
        } else {
            binding.txtNoData.visibility = View.GONE
        }

        // Update list only if catAdapter is initialized
        if (::catAdapter.isInitialized) {
            catAdapter.updateList(filteredList)
        }
    }

    private fun checkAndRequestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
        } else {
            // Permission already granted, you can make the call
            makePhoneCall()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makePhoneCall() {
        val phoneNumber = mobile
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        startActivity(callIntent)
    }

    private fun bottomPopup() {
        val bottomSheet = LocationBottomSheetFragment()

        // Set listener to get value from the bottom sheet
        bottomSheet.setOnItemClickListener(object : LocationBottomSheetFragment.OnItemClickListener {
            override fun onItemSelected(lat_value: String, longi_value: String, locationsss: String, Klm: String) {

                //Preferences
                Preferences.saveStringValue(applicationContext, Preferences.lat,
                    lat_value
                )
                Preferences.saveStringValue(applicationContext, Preferences.longi,
                    longi_value
                )
                Preferences.saveStringValue(applicationContext, Preferences.km,
                    Klm
                )
                Preferences.saveStringValue(applicationContext, Preferences.location,
                    locationsss
                )

                lat = lat_value
                longi = longi_value
                location = locationsss
                Km = Klm
                binding.txtLocation.text = location

                subcategoriesApi()
            }
        })

        bottomSheet.show(supportFragmentManager, "MyBottomSheetFragment")
    }

}