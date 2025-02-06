package com.royalit.garghi.Activitys.Sales

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.royalit.garghi.Activitys.DashBoardActivity
import com.royalit.garghi.AdaptersAndModels.AddProductResponse
import com.royalit.garghi.AdaptersAndModels.DeleteProductImageModel
import com.royalit.garghi.AdaptersAndModels.EditProductImagesListAdapter
import com.royalit.garghi.AdaptersAndModels.ProductItemDetailsModel
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityEditProductBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProductActivity : AppCompatActivity() {

    val binding: ActivityEditProductBinding by lazy {
        ActivityEditProductBinding.inflate(layoutInflater)
    }

    //multi Images selection
    private val REQUEST_CODE_SELECT_IMAGES = 2000
    val imageUris = mutableListOf<Uri>()

    lateinit var product_id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        product_id= intent.getStringExtra("product_id").toString()

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Edit Product"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            productDetailsApi()
        }

        binding.cardMultiImages.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGES)
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
                updateProductApi()
            }
        }

    }

    private fun productDetailsApi() {
        ViewController.showLoading(this@EditProductActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.productDetailsApi(product_id).enqueue(object :
            Callback<ProductItemDetailsModel> {
            override fun onResponse(call: Call<ProductItemDetailsModel>, response: Response<ProductItemDetailsModel>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        if (rsp.status.equals("success")) {
                            productDataSet(rsp)
                        }
                    }
                } else {
                    ViewController.showToast(this@EditProductActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProductItemDetailsModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again: ${t.message}")
            }
        })
    }
    private fun productDataSet(productDetails: ProductItemDetailsModel) {
        binding.productNameEdit.setText(productDetails.data?.product?.product ?: "")
        binding.ActualEdit.setText(productDetails.data?.product?.actual_price ?: "")
        binding.offerPriceEdit.setText(productDetails.data?.product?.offer_price ?: "")
        binding.phoneNumberEdit.setText(productDetails.data?.product?.phone ?: "")
        binding.colorEdit.setText(productDetails.data?.product?.color ?: "")
        binding.brandEdit.setText(productDetails.data?.product?.brand ?: "")
        binding.addressEdit.setText(productDetails.data?.product?.address ?: "")
        binding.featuresEdit.setText(productDetails.data?.product?.features ?: "")
        binding.descriptionEdit.setText(productDetails.data?.product?.description ?: "")

        // Debug the images list
        binding.txtFileName2.text = productDetails.data?.images?.size.toString()+ " - Images"

        productDetails.data?.images?.let { Log.e("img_", it.toString()) }
        if (!productDetails.data?.images.isNullOrEmpty()) {
            val layoutManager = GridLayoutManager(this@EditProductActivity, 3)
            binding.recyclerviewImages.layoutManager = layoutManager
            var galleryimages = productDetails.data?.images?.toMutableList() ?: mutableListOf()
            binding.recyclerviewImages.adapter = EditProductImagesListAdapter(galleryimages) { item ->
                // Handle image item click
                //delete image
                deleteProductImageApi(item.id)
            }
            binding.recyclerviewImages.visibility = View.VISIBLE
            binding.txtNoImages.visibility = View.GONE
        } else {
            binding.recyclerviewImages.visibility = View.GONE
            binding.txtNoImages.visibility = View.VISIBLE
        }

    }
    //delete image
    private fun deleteProductImageApi(id: String?) {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.deleteProductImageApi(id).enqueue(object :
            Callback<DeleteProductImageModel> {
            override fun onResponse(call: Call<DeleteProductImageModel>, response: Response<DeleteProductImageModel>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    if (deleteResponse != null && deleteResponse.message.equals("Image deleted successfully")) {
                        productDetailsApi()
                    }
                }
            }
            override fun onFailure(call: Call<DeleteProductImageModel>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(applicationContext, "Try again")
                Log.e("Tryagain:_ ", t.message.toString())
            }
        })
    }

    //update product
    private fun updateProductApi() {
        val userId = Preferences.loadStringValue(this@EditProductActivity, Preferences.userId, "")
        val locations = Preferences.loadStringValue(this@EditProductActivity, Preferences.location, "")

        Log.e("userId_",userId.toString())

        val productName=binding.productNameEdit.text?.trim().toString()
        val actualPrice=binding.ActualEdit.text?.trim().toString()
        val offerPrice=binding.offerPriceEdit.text?.trim().toString()
        val phoneNumber =binding.phoneNumberEdit.text?.trim().toString()
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
        if(phoneNumber.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Mobile Number")
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

        val productname_ = RequestBody.create(MultipartBody.FORM, productName)
        val actualPrice_ = RequestBody.create(MultipartBody.FORM, actualPrice)
        val offerPrice_ = RequestBody.create(MultipartBody.FORM, offerPrice)
        val phoneNumber_ = RequestBody.create(MultipartBody.FORM, phoneNumber)
        val color_ = RequestBody.create(MultipartBody.FORM, color)
        val brand_ = RequestBody.create(MultipartBody.FORM, brand)
        val address_ = RequestBody.create(MultipartBody.FORM, address)
        val features_ = RequestBody.create(MultipartBody.FORM, features)
        val description_ = RequestBody.create(MultipartBody.FORM, description)
        val userId_ = RequestBody.create(MultipartBody.FORM, userId.toString())
        val product_id_ = RequestBody.create(MultipartBody.FORM, product_id)
        val locations_ = RequestBody.create(MultipartBody.FORM, locations.toString())

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
        apiInterface.updateProductApi(productname_, actualPrice_, offerPrice_, phoneNumber_, color_, brand_, address_, features_, description_, userId_, product_id_, locations_, additionalImages).enqueue(object : Callback<AddProductResponse> {
            override fun onResponse(call: Call<AddProductResponse>, response: Response<AddProductResponse>) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val addResponse = response.body()
                    if (addResponse != null && addResponse.status.equals("success")) {
                        startActivity(Intent(this@EditProductActivity, DashBoardActivity::class.java))
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
        if (requestCode == REQUEST_CODE_SELECT_IMAGES && resultCode == Activity.RESULT_OK) {
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val imageUri = clipData.getItemAt(i).uri
                    imageUris.add(imageUri)
                }
            } else {
                // Single image was selected
                val imageUri = data?.data
                if (imageUri != null) {
                    imageUris.add(imageUri)
                }
            }
            binding.txtFileName2.text = imageUris.size.toString()+ " - Images"
        }
    }


}