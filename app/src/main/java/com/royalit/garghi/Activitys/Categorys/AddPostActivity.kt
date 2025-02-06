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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.royalit.garghi.Activitys.DashBoardActivity
import com.royalit.garghi.AdaptersAndModels.AddPostResponse
import com.royalit.garghi.AdaptersAndModels.CatListAdapter
import com.royalit.garghi.AdaptersAndModels.Categorys.CategoriesModel
import com.royalit.garghi.AdaptersAndModels.Categorys.SubCategoriesModel
import com.royalit.garghi.AdaptersAndModels.ImageAdapter
import com.royalit.garghi.AdaptersAndModels.SubCatListAdapter
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.MapBottomSheetFragment
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityAddPostBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddPostActivity : AppCompatActivity() {

    val binding: ActivityAddPostBinding by lazy {
        ActivityAddPostBinding.inflate(layoutInflater)
    }


    var lat: String = ""
    var longi: String = ""

    //single image selection
    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    //multi Images selection
    private val REQUEST_CODE_SELECT_IMAGES = 2000
    val imageUris = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter

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
            ViewController.showToast(this@AddPostActivity, "Accept permissions")
        }
    }
    var imageType: String = ""

    var categoriesId: String = ""
    var subcategoriesId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Add New Post"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { exitDialog() }

        categoriesApi()
        setupRecyclerView()

        binding.cardImages.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
//            } else {
//                requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
//            }
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
            imageType = "single"
        }

//        binding.cardMultiImages.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
//            } else {
//                requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
//            }
//            imageType = "multi"
//        }

        //multipile images
        binding.addMoreImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
            }

            // Launch the intent
            startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE_SELECT_IMAGES)
            imageType = "multi"
        }

        binding.cardSubmit.setOnClickListener {
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                addPostApi()
            }
        }


        binding.txtLocation.setOnClickListener {
            LocationBottom()
        }

    }

    //categories list
    private fun categoriesApi() {
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
                            categoriesDataSet(categories)
                        }
                    } else {

                    }
                } else {
                    ViewController.showToast(this@AddPostActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<CategoriesModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
            }
        })

    }
    private fun categoriesDataSet(categorysList: List<CategoriesModel>) {
        val adapter = CatListAdapter(this@AddPostActivity, categorysList)
        binding.spinnerCategory.adapter = adapter
        binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedState = parent?.getItemAtPosition(position) as CategoriesModel
                categoriesId = selectedState.category_id
                subCategoriesApi(selectedState.category_id)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }


    //subcategories list
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
                        val categories = response.body()
                        if (categories != null) {
                            subCategoriesDataSet(categories)
                        }
                    } else {
                        subcategoriesId = ""
                    }
                } else {
                    ViewController.showToast(this@AddPostActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<SubCategoriesModel>>, t: Throwable) {
                Log.e("cat_error", t.message.toString())
                subcategoriesId = ""
                // Clear the spinner on failure
                clearSpinner()
            }
        })

    }
    private fun subCategoriesDataSet(subcategorysList: List<SubCategoriesModel>) {
        val adapter = SubCatListAdapter(this@AddPostActivity, subcategorysList)
        binding.spinnerSubCategory.adapter = adapter
        binding.spinnerSubCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedState = parent?.getItemAtPosition(position) as SubCategoriesModel
                subcategoriesId = selectedState.id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    private fun clearSpinner() {
        val emptyAdapter = ArrayAdapter<SubCategoriesModel>(this@AddPostActivity, android.R.layout.simple_spinner_item, ArrayList())
        binding.spinnerSubCategory.adapter = emptyAdapter
    }

    //add post
    private fun addPostApi() {
        val userId = Preferences.loadStringValue(this@AddPostActivity, Preferences.userId, "")
        Log.e("userId_",userId.toString())

        val mapLocation_ =binding.txtLocation.text?.trim().toString()
        val title_ =binding.titleEdit.text?.trim().toString()
        val desctiption_ =binding.desctiptionEdit.text?.trim().toString()
        val phoneNumber_ =binding.phoneNumberEdit.text?.trim().toString()
        val landLineNumber_ =binding.landLineNumberEdit.text?.trim().toString()
        val email_ =binding.emailEdit.text?.trim().toString()
        val address_ =binding.addressEdit.text?.trim().toString()
        val aboutCompany_ =binding.aboutCompanyEdit.text?.trim().toString()
        val servicesList_ =binding.servicesListEdit.text?.trim().toString()


        if(lat.isEmpty() || longi.isEmpty()){
            ViewController.showToast(applicationContext, "select location")
            return
        }
        if(title_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter title")
            return
        }
        if(desctiption_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter description")
            return
        }
        if(phoneNumber_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter phone Number")
            return
        }
        if(landLineNumber_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Land Line Number")
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
        if (selectedImageUri == null) {
            ViewController.showToast(applicationContext, "Please select cover image")
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

        val mapLocation = RequestBody.create(MultipartBody.FORM, mapLocation_)
        val category_id = RequestBody.create(MultipartBody.FORM, categoriesId)
        val subcategory_id = RequestBody.create(MultipartBody.FORM, subcategoriesId)
        val title = RequestBody.create(MultipartBody.FORM, title_)
        val description = RequestBody.create(MultipartBody.FORM, desctiption_)
        val mobile = RequestBody.create(MultipartBody.FORM, phoneNumber_)
        val land_mobile = RequestBody.create(MultipartBody.FORM, landLineNumber_)
        val mail = RequestBody.create(MultipartBody.FORM, email_)
        val address = RequestBody.create(MultipartBody.FORM, address_)
        val about = RequestBody.create(MultipartBody.FORM, aboutCompany_)
        val services = RequestBody.create(MultipartBody.FORM, servicesList_)
        val created_by = RequestBody.create(MultipartBody.FORM, userId.toString())
        val lat_ = RequestBody.create(MultipartBody.FORM, lat)
        val long_ = RequestBody.create(MultipartBody.FORM, longi)

        //cover image
        val file = File(getRealPathFromURI(selectedImageUri!!))
        val requestFile = RequestBody.create(MultipartBody.FORM, file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)


//        //gallery
//        Log.e("img_", imageUris.toString())
//        imageUris.let { Log.e("img_1", it.toString()) }
//        val additionalImages = mutableListOf<MultipartBody.Part>()
//        for (uri in imageUris) {
//            val file = File(getRealPathFromURI(uri))
//            val requestFile = RequestBody.create(MultipartBody.FORM, file)
//            val part = MultipartBody.Part.createFormData("additional_images[]", file.name, requestFile)
//            additionalImages.add(part)
//        }

        val additionalImages = mutableListOf<MultipartBody.Part>()
        for (uri in imageUris) {
            val file = File(getRealPathFromURI(uri))
            val requestFile = RequestBody.create(MultipartBody.FORM, file)
            val part = MultipartBody.Part.createFormData("additional_images[]", file.name, requestFile)
            additionalImages.add(part)
        }


        ViewController.showLoading(this@AddPostActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.addPostApi(mapLocation, category_id, subcategory_id,  title, description, mobile,land_mobile, mail, address, about, services, created_by, lat_, long_, body, additionalImages).enqueue(object :
            Callback<AddPostResponse> {
            override fun onResponse(call: Call<AddPostResponse>, response: Response<AddPostResponse>) {
                ViewController.hideLoading()

                if (response.isSuccessful) {
                    val addResponse = response.body()
                    if (addResponse != null && addResponse.status.equals("success")) {
                        ViewController.showToast(applicationContext, "success")
                        startActivity(Intent(this@AddPostActivity, DashBoardActivity::class.java))
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

        if (imageType.equals("single")){
            //single image selection
            if (resultCode == Activity.RESULT_OK && data != null) {
                selectedImageUri = data.data!!
                val file = File(getRealPathFromURI(selectedImageUri!!))
                binding.txtFileName.text = file.name
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


    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(imageUris) { imageUri ->
            // Remove the selected image
            imageUris.remove(imageUri)
            imageAdapter.updateImages(imageUris)
            binding.txtFileName2.text = "${imageUris.size} - Images"
        }
        binding.recyclerViewImages.layoutManager = GridLayoutManager(this@AddPostActivity, 3)
        binding.recyclerViewImages.adapter = imageAdapter
    }

    private fun LocationBottom() {
        val bottomSheet = MapBottomSheetFragment()
        bottomSheet.setInitialData("", "", "", "") // Example values
        bottomSheet.setOnItemClickListener(object : MapBottomSheetFragment.OnItemClickListener {
            override fun onItemSelected(lat_value: String, longi_value: String, locationsss: String) {

                lat = lat_value
                longi = longi_value
                binding.txtLocation.text = locationsss

            }
        })
        bottomSheet.show(supportFragmentManager, "MyBottomSheetFragment")
    }

    override fun onBackPressed() {
        exitDialog()
    }

    private fun exitDialog(){
        val dialogBuilder = AlertDialog.Builder(this@AddPostActivity)
//        dialogBuilder.setTitle("Exit")
        dialogBuilder.setMessage("Are you sure want to exit this Adding?")
        dialogBuilder.setPositiveButton("Yes", { dialog, whichButton ->
            finish()
            dialog.dismiss()
        })
        dialogBuilder.setNegativeButton("No", { dialog, whichButton ->
            dialog.dismiss()
        })
        val b = dialogBuilder.create()
        b.show()
    }


}