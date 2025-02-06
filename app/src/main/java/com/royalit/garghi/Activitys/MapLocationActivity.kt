package com.royalit.garghi.Activitys

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.royalit.garghi.Config.ViewController
import com.royalit.garghi.R
import com.royalit.garghi.Retrofit.RetrofitClient
import com.royalit.garghi.databinding.ActivityMapLocationBinding

class MapLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    val binding: ActivityMapLocationBinding by lazy {
        ActivityMapLocationBinding.inflate(layoutInflater)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: ArrayAdapter<String>
    private val suggestionsList = mutableListOf<String>()
    private val placeIdList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        ViewController.changeStatusBarColor(this, ContextCompat.getColor(this, R.color.blue), false)

        inits()

    }

    private fun inits() {
        binding.root.findViewById<TextView>(R.id.txtTitle).text = "Location Selection"
        binding.root.findViewById<ImageView>(R.id.imgBack).setOnClickListener { finish() }

        //map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, RetrofitClient.MapKey)
        }
        placesClient = Places.createClient(this)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, suggestionsList)
        binding.listView.adapter = adapter

        // Add a TextWatcher to search location as the user types
        binding.editLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchPlace(query)
                } else {
                    binding.listView.visibility = ListView.GONE
                }
            }
        })

        // Handle list item clicks
        binding.listView.setOnItemClickListener { parent, view, position, id ->
            val selectedPlaceId = placeIdList[position]
            val selectedPlaceName = suggestionsList[position]
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
                    binding.listView.visibility = ListView.VISIBLE
                    adapter.notifyDataSetChanged()
                } else {
                    binding.listView.visibility = ListView.GONE
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
                    Toast.makeText(
                        this,
                        "Selected: $placeName\nLat: ${latLng.latitude}, Lng: ${latLng.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.listView.visibility = ListView.GONE
            }
            .addOnFailureListener { exception ->
                Log.e("MapsActivity", "Place details fetch failed: $exception")
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Picked Location")
            )
            // Move the camera to the picked location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            // You can now use this picked location (latLng.latitude, latLng.longitude)
            Toast.makeText(this, "Location Picked: ${latLng.latitude}, ${latLng.longitude}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(
                    MarkerOptions().position(currentLatLng).title("You are here")
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}