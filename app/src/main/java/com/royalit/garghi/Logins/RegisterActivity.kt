package com.royalit.garghi.Logins

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.royalit.garghi.AdaptersAndModels.Citys.CityAdapter
import com.royalit.garghi.AdaptersAndModels.Citys.CitysModel
import com.royalit.garghi.AdaptersAndModels.RegisterRequest
import com.royalit.garghi.AdaptersAndModels.RegisterResponse
import com.royalit.garghi.AdaptersAndModels.State.StateAdapter
import com.royalit.garghi.AdaptersAndModels.State.StateModel
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(){

    val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    var stateName: String = ""
    var stateId: String = ""
    var cityName: String = ""

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: ArrayAdapter<String>
    private val suggestionsList = mutableListOf<String>()
    private val placeIdList = mutableListOf<String>()

    var SelectLocations: String = ""
    var lat: String = ""
    var longi: String = ""
    var Km: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        stateList()
        citysList()

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

        binding.cardLogin.setOnClickListener{
            if(!ViewController.noInterNetConnectivity(applicationContext)){
                ViewController.showToast(applicationContext, "Please check your connection ")
            }else{
                registerApi()
            }
        }

        binding.loginLinear.setOnClickListener {
            finish()
        }

    }

    private fun registerApi() {
        val name_=binding.nameEdit.text.toString()
        val email=binding.emailEdit.text?.trim().toString()
        val mobileNumber_=binding.mobileEdit.text?.trim().toString()
        val location_= SelectLocations
        val password_=binding.passwordEdit.text?.trim().toString()
        val Cpassword_=binding.CpasswordEdit.text?.trim().toString()

        if(name_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter name")
            return
        }
        if(email.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Email")
            return
        }
        if(mobileNumber_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter mobile number")
            return
        }
        if(location_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Location")
            return
        }

        if(password_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter password")
            return
        }
        if(Cpassword_.isEmpty()){
            ViewController.showToast(applicationContext, "Enter Confirm password")
            return
        }
        if(password_!=Cpassword_){
            ViewController.showToast(applicationContext, "password and confirm password not match")
            return
        }

        val countryCode = binding.ccp.selectedCountryCode

        if (!validateEmail(email)) {
            ViewController.showToast(applicationContext, "Enter Valid Email")
        }else{
            ViewController.showLoading(this@RegisterActivity)

            val apiInterface = RetrofitClient.apiInterface
            val registerRequest = RegisterRequest(name_, email, countryCode.toString(), mobileNumber_,  location_, Km.toString() , lat, longi, password_)

            apiInterface.registerApi(registerRequest).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    ViewController.hideLoading()
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse.status.equals("success")) {
                            ViewController.showToast(applicationContext, "success please Login")
                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        } else {
                            if (loginResponse != null) {
                                ViewController.showToast(applicationContext, loginResponse.message.toString())
                            }
                        }
                    } else {
                        ViewController.showToast(applicationContext, "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    ViewController.hideLoading()
                    ViewController.showToast(applicationContext, "Try again: ${t.message}")
                }

            })

        }
    }

    private fun validateMobileNumber(mobile: String): Boolean {
        val mobilePattern = "^[6-9][0-9]{9}\$"
        return Patterns.PHONE.matcher(mobile).matches() && mobile.matches(Regex(mobilePattern))
    }

    private fun validateEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailPattern))
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
                    ViewController.showToast(this@RegisterActivity, "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: retrofit2.Call<List<StateModel>>, t: Throwable) {
                Log.e("citys_error", t.message.toString())
            }
        })
    }
    private fun stateDataSet(state: List<StateModel>) {
        val adapter = StateAdapter(this@RegisterActivity, state)
        binding.spinnerState.adapter = adapter
        binding.spinnerState.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedState = parent?.getItemAtPosition(position) as StateModel
                stateName = selectedState.state
                stateId = selectedState.id
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
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
                    ViewController.showToast(this@RegisterActivity, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<CitysModel>>, t: Throwable) {
                Log.e("citys_error", t.message.toString())
            }
        })
    }
    private fun CityDataSet(citys: List<CitysModel>) {
        val adapter = CityAdapter(this@RegisterActivity, citys)
        binding.spinnerCity.adapter = adapter
        binding.spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCity = parent?.getItemAtPosition(position) as CitysModel
                cityName = selectedCity.city
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun location() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@RegisterActivity)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(this@RegisterActivity, RetrofitClient.MapKey)
        }
        placesClient = Places.createClient(this@RegisterActivity)
        adapter = ArrayAdapter(this@RegisterActivity, android.R.layout.simple_list_item_1, suggestionsList)
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