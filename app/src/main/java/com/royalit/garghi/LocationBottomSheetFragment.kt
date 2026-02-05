package com.royalit.garghi

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.Slider
import com.royalit.garghi.Retrofit.RetrofitClient


class LocationBottomSheetFragment  : BottomSheetDialogFragment(){

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemSelected(lat_value: String, longi_value: String, location: String, Klm: String)
    }

    private lateinit var listView: ListView
    private lateinit var editLocation: EditText
    private lateinit var kmSlider: Slider

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: ArrayAdapter<String>
    private val suggestionsList = mutableListOf<String>()
    private val placeIdList = mutableListOf<String>()
    var lat: String = ""
    var longi: String = ""
    var Km: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_location_bottom_sheet, container, false)

        listView = view.findViewById(R.id.listView);
        editLocation = view.findViewById(R.id.editLocation);
        kmSlider = view.findViewById(R.id.kmSlider);

        // Assuming you have buttons or views to select a value
        val cardSubmit = view.findViewById<CardView>(R.id.cardSubmit)
        cardSubmit.setOnClickListener {
            val locationText = editLocation.text.toString()
            if (locationText.isNotEmpty()) {
                listener?.onItemSelected(lat, longi, locationText, Km.toString())
                dismiss()
            } else {
                Toast.makeText(requireActivity(), "Select your location", Toast.LENGTH_SHORT).show()
            }
        }

        inits()

        return view

    }


    private fun inits() {

        //radius
        kmSlider.setLabelFormatter { value: Float ->
            // Format the value with "Km" unit
            "${value.toInt()} Km"
        }
        kmSlider.addOnChangeListener { slider, value, fromUser ->
            // You can also handle changes here if needed
            Log.d("Slider", "Value changed to: $value")
            Km = value.toInt()
        }


        //location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), RetrofitClient.MapKey)
        }
        placesClient = Places.createClient(requireActivity())
        adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, suggestionsList)
        listView.adapter = adapter

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
                    listView.visibility = View.GONE
                }

            }
        }

        // Attach the TextWatcher to the EditText
        editLocation.addTextChangedListener(textWatcher)

        // Handle list item clicks
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedPlaceId = placeIdList[position]
            val selectedPlaceName = suggestionsList[position]

            editLocation.removeTextChangedListener(textWatcher)

            editLocation.setText(selectedPlaceName)

            editLocation.addTextChangedListener(textWatcher)

            listView.visibility = View.GONE
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
                   listView.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                } else {
                    listView.visibility = View.GONE
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
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapsActivity", "Place details fetch failed: $exception")
            }
    }


    //set the listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}