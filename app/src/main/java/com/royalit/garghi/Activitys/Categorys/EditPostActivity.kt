package com.royalit.garghi.Activitys.Categorys

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.royalit.garghi.Activitys.DashBoardActivity
import com.royalit.garghi.AdaptersAndModels.AddPostResponse
import com.royalit.garghi.AdaptersAndModels.CatListAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesModel
import com.royalit.garghi.AdaptersAndModels.Categorys.SubCategoriesModel
import com.royalit.garghi.AdaptersAndModels.DeletePostImageModel
import com.royalit.garghi.AdaptersAndModels.EditPostImagesListAdapter
import com.royalit.garghi.AdaptersAndModels.PostItemDetailsModel
import com.royalit.garghi.AdaptersAndModels.SubCatListAdapter
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.MapBottomSheetFragment
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityEditPostBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditPostActivity : AppCompatActivity() {

    val binding: ActivityEditPostBinding by lazy {
        ActivityEditPostBinding.inflate(layoutInflater)
    }

    lateinit var post_id:String

    //single image selection
    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    //multi Images selection
    private val REQUEST_CODE_SELECT_IMAGES = 2000
    val imageUris = mutableListOf<Uri>()

    var categoriesId: String = ""
    var subcategoriesId: String = ""

    val requestPermissions = registerForActivityResult(RequestMultiplePermissions()) { results ->
        var permission = false;
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            (
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        READ_MEDIA_IMAGES
                    ) == PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                applicationContext,
                                READ_MEDIA_VIDEO
                            ) == PERMISSION_GRANTED
                    )
        ) {
            permission = true
            // Full access on Android 13 (API level 33) or higher
        } else if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                READ_MEDIA_VISUAL_USER_SELECTED
            ) == PERMISSION_GRANTED
        ) {
            permission = true
            // Partial access on Android 14 (API level 34) or higher
        } else if (ContextCompat.checkSelfPermission(
                applicationContext,
                READ_EXTERNAL_STORAGE
            ) == PERMISSION_GRANTED
        ) {
            permission = true
            // Full access up to Android 12 (API level 32)
        } else {
            permission = false
        }
        if (permission) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        } else {
            ViewController.showToast(this@EditPostActivity, "Accept permissions")
        }
    }
    var imageType: String = ""

    var lat: String = ""
    var longi: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        post_id= intent.getStringExtra("post_id").toString()

        Log.e("post_id",post_id)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Edit Post"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            categoriesItemsDetailsApi()
        }

        binding.txtLocation.setOnClickListener {
            LocationBottom()
        }

        binding.cardImages.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
            } else {
                requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
            }
            imageType = "single"
        }

        binding.cardMultiImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
            }

            // Launch the intent
            startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE_SELECT_IMAGES)
            imageType = "multi"
        }
        binding.addMoreImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES)
        }

        binding.cardSubmit.setOnClickListener {
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                updatePostApi()
            }
        }
    }


    private fun categoriesItemsDetailsApi() {
        ViewController.showLoading(this@EditPostActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.categoriesItemsDetailsApi(post_id).enqueue(object :
            Callback<PostItemDetailsModel> {
            override fun onResponse(call: Call<PostItemDetailsModel>, response: Response<PostItemDetailsModel>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        if (rsp.status.equals("success")) {
                            postDataSet(rsp)
                        }
                    }
                } else {
                    ViewController.showToast(this@EditPostActivity, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<PostItemDetailsModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })
    }

    private fun postDataSet(postDetails: PostItemDetailsModel) {
        categoriesId = postDetails.data?.product?.categoryId ?: ""
        subcategoriesId = postDetails.data?.product?.subcategory ?: ""
        binding.txtLocation.setText(postDetails.data?.product?.location ?: "")
        binding.titleEdit.setText(postDetails.data?.product?.title ?: "")
        binding.DesctiptionEdit.setText(postDetails.data?.product?.description ?: "")
        binding.phoneNumberEdit.setText(postDetails.data?.product?.mobile ?: "")
        binding.LandNumberEdit.setText(postDetails.data?.product?.landline ?: "")
        binding.emailEdit.setText(postDetails.data?.product?.mail ?: "")
        binding.addressEdit.setText(postDetails.data?.product?.address ?: "")
        binding.aboutCompanyEdit.setText(postDetails.data?.product?.about ?: "")
        binding.servicesListEdit.setText(postDetails.data?.product?.services ?: "")

        Log.e("categoriesId_abc",categoriesId);

        categoriesApi()

        lat = postDetails.data?.product?.latitude ?: ""
        longi = postDetails.data?.product?.longitude ?: ""

        // Load cover image
        Glide.with(binding.imgCover)
            .load(RetrofitClient.Image_Path + postDetails.data?.product?.image)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imgCover)

        // Debug the images list
        binding.txtFileName2.text = postDetails.data?.images?.size.toString()+ " - Images"

        postDetails.data?.images?.let { Log.e("img_", it.toString()) }
        if (!postDetails.data?.images.isNullOrEmpty()) {
            val layoutManager = GridLayoutManager(this@EditPostActivity, 3)
            binding.recyclerviewImages.layoutManager = layoutManager
            var galleryimages = postDetails.data?.images?.toMutableList() ?: mutableListOf()
            binding.recyclerviewImages.adapter = EditPostImagesListAdapter(galleryimages) { item ->
                //delete image
                deletePostImageApi(item.id)
            }
            binding.recyclerviewImages.visibility = View.VISIBLE
            binding.txtNoImages.visibility = View.GONE
        } else {
            binding.recyclerviewImages.visibility = View.GONE
            binding.txtNoImages.visibility = View.VISIBLE
        }
    }

    //delete image
    private fun deletePostImageApi(id: String?) {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.deletePostImageApi(id).enqueue(object :
            Callback<DeletePostImageModel> {
            override fun onResponse(call: Call<DeletePostImageModel>, response: Response<DeletePostImageModel>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                        if (deleteResponse != null && deleteResponse.message.equals("Image deleted successfully")) {
                            categoriesItemsDetailsApi()
                        }
                }
            }
            override fun onFailure(call: Call<DeletePostImageModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again")
                Log.e("Tryagain:_ ", t.message.toString())
            }
        })
    }


    //update Post Api
    private fun updatePostApi() {
        val userId = Preferences.loadStringValue(this@EditPostActivity, Preferences.userId, "")
        Log.e("userId_",userId.toString())

        val mapLocation_ =binding.txtLocation.text?.trim().toString()
        val title_ =binding.titleEdit.text?.trim().toString()
        val desctiption_ =binding.DesctiptionEdit.text?.trim().toString()
        val phoneNumber_ =binding.phoneNumberEdit.text?.trim().toString()
        val landline_ =binding.LandNumberEdit.text?.trim().toString()
        val email_ =binding.emailEdit.text?.trim().toString()
        val address_ =binding.addressEdit.text?.trim().toString()
        val aboutCompany_ =binding.aboutCompanyEdit.text?.trim().toString()
        val servicesList_ =binding.servicesListEdit.text?.trim().toString()


        if(mapLocation_.isEmpty()){
            ViewController.showToast(applicationContext, "select Location")
            return
        }
        if(lat.isEmpty()){
            ViewController.showToast(applicationContext, "select Location")
            return
        }
        if(title_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter title")
            return
        }
        if(desctiption_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter desctiption")
            return
        }
        if(phoneNumber_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter phone Number")
            return
        }
        if(landline_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Landline Number")
            return
        }
        if(email_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter email")
            return
        }
        if(address_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter address")
            return
        }
        if(aboutCompany_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter aboutCompany")
            return
        }
        if(servicesList_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter services List")
            return
        }

        if (!validateEmail(email_)) {
            ViewController.showToast(applicationContext, "Enter Valid email")
            return
        }
        if (!validateMobileNumber(phoneNumber_)) {
            ViewController.showToast(applicationContext, "Enter Valid mobile number")
            return
        }
        if(categoriesId.isEmpty()){
            ViewController.showToast(applicationContext, "Select  Categories")
            return
        }
        if(subcategoriesId.isEmpty()){
            ViewController.showToast(applicationContext, "Select SubCategories")
            return
        }

        val location_ = RequestBody.create(MultipartBody.FORM, mapLocation_)
        val category_id = RequestBody.create(MultipartBody.FORM, categoriesId)
        val subcategory = RequestBody.create(MultipartBody.FORM, subcategoriesId)
        val title = RequestBody.create(MultipartBody.FORM, title_)
        val description = RequestBody.create(MultipartBody.FORM, desctiption_)
        val mobile = RequestBody.create(MultipartBody.FORM, phoneNumber_)
        val landline = RequestBody.create(MultipartBody.FORM, landline_)
        val mail = RequestBody.create(MultipartBody.FORM, email_)
        val address = RequestBody.create(MultipartBody.FORM, address_)
        val about = RequestBody.create(MultipartBody.FORM, aboutCompany_)
        val services = RequestBody.create(MultipartBody.FORM, servicesList_)
        val created_by = RequestBody.create(MultipartBody.FORM, userId.toString())
        val post_id_ = RequestBody.create(MultipartBody.FORM, post_id)
        val lat_ = RequestBody.create(MultipartBody.FORM, lat)
        val long_ = RequestBody.create(MultipartBody.FORM, longi)


        //cover image
        val body: MultipartBody.Part
        if (selectedImageUri != null) {
            val file = File(getRealPathFromURI(selectedImageUri!!))
            val requestFile = RequestBody.create(MultipartBody.FORM, file)
            body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        } else {
            //send empty image
            body = coverEmptyImagePart()
        }

        //gallery
        val additionalImages = mutableListOf<MultipartBody.Part>()
        if (imageUris.size==0 || imageUris==null){
            additionalImages.clear()
        }else{
            for (uri in imageUris) {
                val file = File(getRealPathFromURI(uri))
                val requestFile = RequestBody.create(MultipartBody.FORM, file)
                val part = MultipartBody.Part.createFormData("additional_images[]", file.name, requestFile)
                additionalImages.add(part)
            }
        }

        val apiInterface = RetrofitClient.apiInterface
        apiInterface.updatePostApi(location_, category_id, subcategory, title, description, mobile, landline, mail, address, about, services, created_by, post_id_, lat_, long_, body, additionalImages).enqueue(object :
            Callback<AddPostResponse> {
            override fun onResponse(call: Call<AddPostResponse>, response: Response<AddPostResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val addResponse = response.body()
                    if (addResponse != null && addResponse.status.equals("success")) {
                        startActivity(Intent(this@EditPostActivity, DashBoardActivity::class.java))
                    } else {
                        ViewController.showToast(applicationContext, "Failed")
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<AddPostResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
                Log.e("Tryagain:_ ", t.message.toString())
            }
        })

    }

    private fun validateMobileNumber(mobile: String): Boolean {
        val mobilePattern = "^[6-9][0-9]{9}\$"
        return Patterns.PHONE.matcher(mobile).matches() && mobile.matches(Regex(mobilePattern))
    }
    private fun validateEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //single image selection

        if (imageType.equals("single")){
            //single image selection
            if (resultCode == Activity.RESULT_OK && data != null) {
                selectedImageUri = data.data!!
            }
        }else{
            //multi image selection
            if (resultCode == Activity.RESULT_OK) {
                val clipData = data?.clipData
                var invalidImageCount = 0

                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        if (isValidImage(imageUri)) {
                            if (!imageUris.contains(imageUri)) {
                                imageUris.add(imageUri)
                            }
                        } else {
                            invalidImageCount++
                        }
                    }
                } else {
                    // Handle single image selection
                    val imageUri = data?.data
                    if (imageUri != null) {
                        if (isValidImage(imageUri)) {
                            if (!imageUris.contains(imageUri)) {
                                imageUris.add(imageUri)
                            }
                        } else {
                            invalidImageCount++
                        }
                    }
                }
                binding.txtFileName2.text = "${imageUris.size} - Images added"

                // Show a summary of invalid images
                if (invalidImageCount > 0) {
                    ViewController.showToast(
                        applicationContext,
                        "$invalidImageCount invalid image(s) ignored"
                    )
                }
            }
        }
    }
    private fun isValidImage(uri: Uri): Boolean {
        val mimeType = contentResolver.getType(uri)
        return mimeType == "image/jpeg" || mimeType == "image/png"
    }

    private fun coverEmptyImagePart(): MultipartBody.Part {
        // Create an empty RequestBody
        val requestFile = RequestBody.create(MultipartBody.FORM, ByteArray(0))
        return MultipartBody.Part.createFormData("image", "", requestFile)
    }

    //location
    private fun LocationBottom() {
        val bottomSheet = MapBottomSheetFragment()
        //bottomSheet.setInitialData(lat, longi, binding.txtLocation.text?.trim().toString(), "edit") // Example values
        bottomSheet.setInitialData("", "", "", "edit") // Example values
        bottomSheet.setOnItemClickListener(object : MapBottomSheetFragment.OnItemClickListener {
            override fun onItemSelected(lat_value: String, longi_value: String, locationsss: String) {

                lat = lat_value
                longi = longi_value
                binding.txtLocation.text = locationsss

            }
        })
        bottomSheet.show(supportFragmentManager, "MyBottomSheetFragment")
    }


    //categories list
    // API Call to get categories
    private fun categoriesApi() {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.categoriesApi().enqueue(object : retrofit2.Callback<List<CategoriesModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<CategoriesModel>>,
                response: retrofit2.Response<List<CategoriesModel>>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val categories = response.body()
                    if (categories != null) {
                        // Now call categoriesDataSet() to set up the adapter and spinner
                        categoriesDataSet(categories)
                    } else {
                        // Handle case where categories are null (maybe show a message)
                        ViewController.showToast(this@EditPostActivity, "Categories are empty.")
                    }
                } else {
                    ViewController.showToast(this@EditPostActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<CategoriesModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
            }
        })
    }

    // Set up categories and handle the Spinner selection
    private fun categoriesDataSet(categorysList: List<CategoriesModel>) {
        val adapter = CatListAdapter(this@EditPostActivity, categorysList)
        binding.spinnerCategory.adapter = adapter

        // Check if categoriesId is a valid category_id (assumed to be a String here)
        val selectedPosition = categorysList.indexOfFirst { it.category_id == categoriesId }

        // Set the selection if a valid position is found
        if (selectedPosition != -1) {
            binding.spinnerCategory.setSelection(selectedPosition)
        } else {
            // Optionally handle the case where the category_id doesn't match any item
            Log.d("Categories", "No matching category found for categoriesId: $categoriesId")
        }

        // Handle item selection from spinner
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = parent?.getItemAtPosition(position) as CategoriesModel
                categoriesId = selectedCategory.category_id
                subCategoriesApi(categoriesId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: Handle when no selection is made
            }
        }
    }


    //subcategories list
    // API call to get subcategories
    private fun subCategoriesApi(catId: String) {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.subcategoriesApi(catId).enqueue(object : retrofit2.Callback<List<SubCategoriesModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<SubCategoriesModel>>,
                response: retrofit2.Response<List<SubCategoriesModel>>
            ) {
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        // Log the response to check the data
                        Log.d("SubCategories", "SubCategories Response: ${rsp.size} items found")
                        if (rsp.isNotEmpty()) {
                            // Proceed to update the subcategory spinner
                            subCategoriesDataSet(rsp)
                        } else {
                            // Handle empty response (no subcategories)
                            subcategoriesId = "" // Clear subcategory selection
                            ViewController.showToast(this@EditPostActivity, "No subcategories available.")
                        }
                    } else {
                        // Handle null response body
                        ViewController.showToast(this@EditPostActivity, "Subcategories response is null.")
                    }
                } else {
                    // Handle unsuccessful response
                    ViewController.showToast(this@EditPostActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<SubCategoriesModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                subcategoriesId = "" // Clear the spinner
                // Handle the failure (e.g., clear spinner, show error)
                clearSpinner()
            }
        })
    }

    // Set subcategories to the spinner
    private fun subCategoriesDataSet(subcategorysList: List<SubCategoriesModel>) {
        if (subcategorysList.isEmpty()) {
            // If no subcategories, show an empty message or handle appropriately
            binding.spinnerSubCategory.visibility = View.GONE // Hide spinner if no data
            ViewController.showToast(this@EditPostActivity, "No subcategories available.")
            return
        }

        // Show the spinner if there are subcategories
        binding.spinnerSubCategory.visibility = View.VISIBLE

        val adapter = SubCatListAdapter(this@EditPostActivity, subcategorysList)
        binding.spinnerSubCategory.adapter = adapter

        // Find the position of the selected subcategory
        val selectedPosition = subcategorysList.indexOfFirst { it.id == subcategoriesId }

        // Set the selection if a valid position is found
        if (selectedPosition != -1) {
            binding.spinnerSubCategory.setSelection(selectedPosition)
        } else {
            // Handle case where subcategoriesId doesn't match any item
            Log.d("SubCategories", "No matching subcategory found for subcategoriesId: $subcategoriesId")
        }

        // Handle item selection from the subcategory spinner
        binding.spinnerSubCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSubCategory = parent?.getItemAtPosition(position) as SubCategoriesModel
                subcategoriesId = selectedSubCategory.id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun clearSpinner() {
        val emptyAdapter = ArrayAdapter<SubCategoriesModel>(this@EditPostActivity, android.R.layout.simple_spinner_item, ArrayList())
        binding.spinnerSubCategory.adapter = emptyAdapter
    }

}