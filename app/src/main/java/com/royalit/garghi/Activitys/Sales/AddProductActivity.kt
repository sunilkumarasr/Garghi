package com.royalit.garghi.Activitys.Sales

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
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.royalit.garghi.Activitys.DashBoardActivity
import com.royalit.garghi.AdaptersAndModels.AddProductResponse
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityAddProductBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddProductActivity : AppCompatActivity() {

    val binding: ActivityAddProductBinding by lazy {
        ActivityAddProductBinding.inflate(layoutInflater)
    }

    //single image selection
    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    //multi Images selection
    private val REQUEST_CODE_SELECT_IMAGES = 2000
    val imageUris = mutableListOf<Uri>()

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
            ViewController.showToast(this@AddProductActivity, "Accept permissions")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Add Product"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { exitDialog() }

        binding.addMoreImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple selections
            }

            // Launch the intent
            startActivityForResult(Intent.createChooser(intent, "Select Images"), REQUEST_CODE_SELECT_IMAGES)
        }

        binding.cardSubmit.setOnClickListener {
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                addProductApi()
            }
        }
    }


    private fun addProductApi() {
        val userId = Preferences.loadStringValue(this@AddProductActivity, Preferences.userId, "")
        val locations = Preferences.loadStringValue(this@AddProductActivity, Preferences.location, "")
        Log.e("userId_",userId.toString())

        val productName=binding.productNameEdit.text?.trim().toString()
        val actualPrice=binding.ActualEdit.text?.trim().toString()
        val offerPrice=binding.offerPriceEdit.text?.trim().toString()
        val phoneNumber_ =binding.phoneNumberEdit.text?.trim().toString()
        val color=binding.colorEdit.text?.trim().toString()
        val brand=binding.brandEdit.text?.trim().toString()
        val address=binding.addressEdit.text?.trim().toString()
        val features=binding.featuresEdit.text?.trim().toString()
        val description=binding.descriptionEdit.text?.trim().toString()


        if(productName.isEmpty()){
            ViewController.showToast(applicationContext, "Enter product name")
            return
        }
        if(actualPrice.isEmpty()){
            ViewController.showToast(applicationContext, "Enter actual price")
            return
        }
        if(offerPrice.isEmpty()){
            ViewController.showToast(applicationContext, "Enter offer price")
            return
        }
        if(phoneNumber_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter phone Number")
            return
        }
        if(color.isEmpty()){
            ViewController.showToast(applicationContext, "Enter colour")
            return
        }
        if(brand.isEmpty()){
            ViewController.showToast(applicationContext, "Enter brand")
            return
        }
        if(address.isEmpty()){
            ViewController.showToast(applicationContext, "Enter address")
            return
        }
        if(features.isEmpty()){
            ViewController.showToast(applicationContext, "Enter features")
            return
        }
        if(description.isEmpty()){
            ViewController.showToast(applicationContext, "Enter description")
            return
        }
        if (!validateMobileNumber(phoneNumber_)) {
            ViewController.showToast(applicationContext, "Enter Valid mobile number")
            return
        }
        if(imageUris.size == 0){
            ViewController.showToast(applicationContext, "Please select Images")
            return
        }


        val productname_ = RequestBody.create(MultipartBody.FORM, productName)
        val actualPrice_ = RequestBody.create(MultipartBody.FORM, actualPrice)
        val offerPrice_ = RequestBody.create(MultipartBody.FORM, offerPrice)
        val mobile = RequestBody.create(MultipartBody.FORM, phoneNumber_)
        val color_ = RequestBody.create(MultipartBody.FORM, color)
        val brand_ = RequestBody.create(MultipartBody.FORM, brand)
        val address_ = RequestBody.create(MultipartBody.FORM, address)
        val features_ = RequestBody.create(MultipartBody.FORM, features)
        val description_ = RequestBody.create(MultipartBody.FORM, description)
        val userId_ = RequestBody.create(MultipartBody.FORM, userId.toString())
        val locations_ = RequestBody.create(MultipartBody.FORM, locations.toString())


        val additionalImages = mutableListOf<MultipartBody.Part>()
        for (uri in imageUris) {
            val file = File(getRealPathFromURI(uri))
            val requestFile = RequestBody.create(MultipartBody.FORM, file)
            val part = MultipartBody.Part.createFormData("additional_images[]", file.name, requestFile)
            additionalImages.add(part)
        }

        ViewController.showLoading(this@AddProductActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.addProductApi(productname_, actualPrice_, offerPrice_, mobile, color_, brand_, address_, features_, description_, userId_, locations_, additionalImages).enqueue(object : Callback<AddProductResponse> {
            override fun onResponse(call: Call<AddProductResponse>, response: Response<AddProductResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val addResponse = response.body()
                    if (addResponse != null && addResponse.status.equals("success")) {
                        startActivity(Intent(this@AddProductActivity, DashBoardActivity::class.java))
                    } else {
                        ViewController.showToast(applicationContext, "Failed")
                    }
                } else {
                    ViewController.showToast(applicationContext, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<AddProductResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
                Log.e("Tryagain:_ ", t.message.toString())
            }
        })
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
    private fun isValidImage(uri: Uri): Boolean {
        val mimeType = contentResolver.getType(uri)
        return mimeType == "image/jpeg" || mimeType == "image/png"
    }

    private fun validateMobileNumber(mobile: String): Boolean {
        val mobilePattern = "^[6-9][0-9]{9}\$"
        return Patterns.PHONE.matcher(mobile).matches() && mobile.matches(Regex(mobilePattern))
    }

    override fun onBackPressed() {
        exitDialog()
    }

    private fun exitDialog(){
        val dialogBuilder = AlertDialog.Builder(this@AddProductActivity)
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