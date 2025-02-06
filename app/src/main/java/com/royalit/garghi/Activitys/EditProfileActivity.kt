package com.royalit.garghi.Activitys

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.royalit.garghi.AdaptersAndModels.Citys.CitysModel
import com.royalit.garghi.AdaptersAndModels.ProfileResponse
import com.royalit.garghi.AdaptersAndModels.State.StateModel
import com.royalit.garghi.AdaptersAndModels.UpdateProfileResponse
import com.royalit.garghi.Config.Preferences
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityEditProfileBinding
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    val binding: ActivityEditProfileBinding by lazy {
        ActivityEditProfileBinding.inflate(layoutInflater)
    }

//    val requestPermissions = registerForActivityResult(RequestMultiplePermissions()) { results ->
//        var permission = false;
//        if (
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
//            (
//                    ContextCompat.checkSelfPermission(
//                        applicationContext,
//                        READ_MEDIA_IMAGES
//                    ) == PERMISSION_GRANTED ||
//                            ContextCompat.checkSelfPermission(
//                                applicationContext,
//                                READ_MEDIA_VIDEO
//                            ) == PERMISSION_GRANTED
//                    )
//        ) {
//            permission = true
//            // Full access on Android 13 (API level 33) or higher
//        } else if (
//            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
//            ContextCompat.checkSelfPermission(
//                applicationContext,
//                READ_MEDIA_VISUAL_USER_SELECTED
//            ) == PERMISSION_GRANTED
//        ) {
//            permission = true
//            // Partial access on Android 14 (API level 34) or higher
//        } else if (ContextCompat.checkSelfPermission(
//                applicationContext,
//                READ_EXTERNAL_STORAGE
//            ) == PERMISSION_GRANTED
//        ) {
//            permission = true
//            // Full access up to Android 12 (API level 32)
//        } else {
//            permission = false
//        }
//        if (permission) {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(intent, IMAGE_PICK_CODE)
//        } else {
//            ViewController.showToast(this@EditProfileActivity, "Accept permissions")
//        }
//    }

    //image selection
    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

    var stateId: String = ""
    var cityId: String = ""


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: ArrayAdapter<String>
    private val suggestionsList = mutableListOf<String>()
    private val placeIdList = mutableListOf<String>()

    var SelectLocations: String = ""
    var lat: String = ""
    var longi: String = ""
    var Km: Int = 0
    var c: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.bottom_myservice), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Edit Profile"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        if(!ViewController.noInterNetConnectivity(applicationContext)){
            ViewController.showToast(applicationContext, "Please check your connection ")
        }else{
            getProfileApi()
        }

        location()




        binding.kmSlider.setLabelFormatter { value: Float ->
            // Format the value with "Km" unit
            "${value.toInt()} Km"
        }
        binding.kmSlider.addOnChangeListener { slider, value, fromUser ->
            // You can also handle changes here if needed
            Log.d("Slider", "Value changed to: $value")
            Km = value.toInt()
        }


        binding.cardChoose.setOnClickListener {
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
        }

        binding.cardUpdate.setOnClickListener {
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                updateProfileApi()
            }
        }

        binding.changeLocation.setOnClickListener {
            c = "1"
            binding.linearLocation.visibility = View.GONE
            binding.linearchangeLocation.visibility = View.VISIBLE
        }

        binding.clearLocation.setOnClickListener {
            c = "0"
            binding.linearLocation.visibility = View.VISIBLE
            binding.linearchangeLocation.visibility = View.GONE
        }


    }

    private fun getProfileApi() {
        val userId = Preferences.loadStringValue(this@EditProfileActivity, Preferences.userId, "")
        Log.e("userId_", userId.toString())

        ViewController.showLoading(this@EditProfileActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.getProfileApi(userId).enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(
                call: Call<ProfileResponse>,
                response: Response<ProfileResponse>
            ) {
                ViewController.hideLoading()
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        //state and city
                        stateId = rsp.data?.state.toString()
                        cityId = rsp.data?.city.toString()
                        stateList()
                        citysList()


                        lat = rsp.data?.latitude.toString()
                        longi = rsp.data?.longitude.toString()
                        Km = rsp.data?.km?.toIntOrNull() ?: 0

                        val sliderValue = when {
                            Km < 1 -> 1f
                            Km > 100 -> 100f
                            else -> Km.toFloat()
                        }
                        binding.kmSlider.value = sliderValue

                        binding.nameEdit.setText(rsp.data?.name)
                        binding.emailEdit.setText(rsp.data?.email)
                        binding.mobileEdit.setText(rsp.data?.phone)
                        rsp.data?.country_code?.toInt()
                            ?.let { binding.ccp.setCountryForPhoneCode(it) }
                        binding.setLocation.setText(rsp.data?.location)
                        SelectLocations = rsp.data?.location.toString()
                        //binding.editLocations.setText(rsp.data?.location)
                        if (!rsp.data?.image.equals("")) {
                            Glide.with(binding.profileImage).load(rsp.data?.image)
                                .into(binding.profileImage)
                        }

                    }
                } else {
                    ViewController.showToast(this@EditProfileActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                ViewController.hideLoading()
                ViewController.showToast(this@EditProfileActivity, "Try again: ${t.message}")
            }
        })
    }

    private fun updateProfileApi() {
        val userId = Preferences.loadStringValue(this@EditProfileActivity, Preferences.userId, "")

        val name = binding.nameEdit.text?.trim().toString()
        val email = binding.emailEdit.text?.trim().toString()
        val mobile = binding.mobileEdit.text?.trim().toString()
        val location = SelectLocations

        // Validate inputs
        if (name.isEmpty()) {
            ViewController.showToast(applicationContext, "Enter name")
            return
        }
        if (email.isEmpty()) {
            ViewController.showToast(applicationContext, "Enter email")
            return
        }
        if (mobile.isEmpty()) {
            ViewController.showToast(applicationContext, "Enter mobile")
            return
        }

        if (c.equals("1")) {
            if (binding.editLocations.text?.trim().toString().isEmpty()) {
                ViewController.showToast(applicationContext, "Select Location")
                return
            }
        }

        val countryCode = binding.ccp.selectedCountryCode

        // Prepare form data
        val userId_ = RequestBody.create(MultipartBody.FORM, userId.toString())
        val name_ = RequestBody.create(MultipartBody.FORM, name)
        val email_ = RequestBody.create(MultipartBody.FORM, email)
        val countryCode_ = RequestBody.create(MultipartBody.FORM, countryCode.toString())
        val mobile_ = RequestBody.create(MultipartBody.FORM, mobile)
        val location_ = RequestBody.create(MultipartBody.FORM, location)
        val lat_ = RequestBody.create(MultipartBody.FORM, lat)
        val longi_ = RequestBody.create(MultipartBody.FORM, longi)
        val Km_ = RequestBody.create(MultipartBody.FORM, Km.toString())

        val body: MultipartBody.Part
        if (selectedImageUri != null) {
            val file = File(getRealPathFromURI(selectedImageUri!!))
            val requestFile = RequestBody.create(MultipartBody.FORM, file)
            body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        } else {
            //send empty image
            body = createEmptyImagePart()
        }

        ViewController.showLoading(this@EditProfileActivity)
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.updateProfileApi(userId_, name_, email_, countryCode_, mobile_ , location_, lat_, longi_, Km_,  body)
            .enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val addResponse = response.body()
                        if (addResponse != null) {
                            startActivity(
                                Intent(this@EditProfileActivity, DashBoardActivity::class.java)
                            )
                        }
                    } else {
                        ViewController.showToast(applicationContext, "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    ViewController.hideLoading()
                    ViewController.showToast(applicationContext, "Try again: ${t.message}")
                    Log.e("Tryagain:_ ", t.message.toString())
                }
            })

    }


    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //single image selection
        if (data != null) {
            selectedImageUri = data.data!!
            val file = File(getRealPathFromURI(selectedImageUri!!))
            binding.txtFileName.text = file.name
        }

    }

    //update profile
    private fun createEmptyImagePart(): MultipartBody.Part {
        // Create an empty RequestBody
        val requestFile = RequestBody.create(MultipartBody.FORM, ByteArray(0))
        return MultipartBody.Part.createFormData("image", "", requestFile)
    }

    private fun validateMobileNumber(mobile: String): Boolean {
        val mobilePattern = "^[6-9][0-9]{9}\$"
        return Patterns.PHONE.matcher(mobile).matches() && mobile.matches(Regex(mobilePattern))
    }

    private fun stateList() {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.stateListApi().enqueue(object : retrofit2.Callback<List<StateModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<StateModel>>,
                response: retrofit2.Response<List<StateModel>>
            ) {
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        val stateList = response.body()
                        if (stateList != null) {
                            stateDataSet(stateList)
                        }
                    } else {

                    }
                } else {
                    ViewController.showToast(this@EditProfileActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<StateModel>>, t: Throwable) {
                Log.e("citys_error", t.message.toString())
            }
        })
    }
    private fun stateDataSet(state: List<StateModel>) {

        for (i in state.indices) {
            if (state[i].id.equals(stateId)) {
                binding.spinnerState.text = state[i].state
            }
        }

    }

    private fun citysList() {
        val apiInterface = RetrofitClient.apiInterface
        apiInterface.cityApi(stateId).enqueue(object : retrofit2.Callback<List<CitysModel>> {
            override fun onResponse(
                call: retrofit2.Call<List<CitysModel>>,
                response: retrofit2.Response<List<CitysModel>>
            ) {
                if (response.isSuccessful) {
                    val rsp = response.body()
                    if (rsp != null) {
                        val cityList = response.body()
                        if (cityList != null) {
                            CityDataSet(cityList)
                        }
                    } else {

                    }
                } else {
                    ViewController.showToast(this@EditProfileActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<CitysModel>>, t: Throwable) {
                Log.e("citys_error", t.message.toString())
            }
        })
    }
    private fun CityDataSet(citys: List<CitysModel>) {
        for (i in citys.indices) {
            if (citys[i].id.equals(cityId)) {
                binding.spinnerCity.text = citys[i].city
            }
        }
    }


    private fun location() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@EditProfileActivity)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(this@EditProfileActivity, RetrofitClient.MapKey)
        }
        placesClient = Places.createClient(this@EditProfileActivity)
        adapter = ArrayAdapter(this@EditProfileActivity, android.R.layout.simple_list_item_1, suggestionsList)
        binding.listViews.adapter = adapter

        // Add TextWatcher to search location as the user types
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchPlace(query)
                }else{
                    binding.listViews.visibility = View.GONE
                }

            }
        }

        // Attach the TextWatcher to the EditText
        binding.editLocations.addTextChangedListener(textWatcher)

        // Handle list item clicks
        binding.listViews.setOnItemClickListener { parent, view, position, id ->
            val selectedPlaceId = placeIdList[position]
            val selectedPlaceName = suggestionsList[position]

            // Fetch place details and set the selected location
            // fetchPlaceDetails(selectedPlaceId, selectedPlaceName)

            // Remove TextWatcher before updating EditText to avoid triggering it
            binding.editLocations.removeTextChangedListener(textWatcher)

            // Set the selected place in the EditText
            binding.editLocations.setText(selectedPlaceName)


            // Reattach the TextWatcher after setting the text
            binding.editLocations.addTextChangedListener(textWatcher)

            // Hide the ListView after selecting a location
            binding.listViews.visibility = View.GONE

            fetchPlaceDetails(selectedPlaceId, selectedPlaceName)
        }
    }
    private fun searchPlace(query: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                suggestionsList.clear()
                placeIdList.clear()

                for (prediction in response.autocompletePredictions) {
                    suggestionsList.add(prediction.getFullText(null).toString())
                    placeIdList.add(prediction.placeId)
                }

                if (suggestionsList.isNotEmpty()) {
                    binding.listViews.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                } else {
                    binding.listViews.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapsActivity", "Place search failed: $exception")
            }
    }
    private fun fetchPlaceDetails(placeId: String, placeName: String) {
        val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)

        val request = com.google.android.libraries.places.api.net.FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val place = response.place
                val latLng = place.latLng
                if (latLng != null) {
                    lat = latLng.latitude.toString()
                    longi = latLng.longitude.toString()
                    SelectLocations = placeName
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapsActivity", "Place details fetch failed: $exception")
            }
    }

}