package com.royalit.garghi

import android.app.Dialog
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.royalit.garghi.Retrofit.RetrofitClient

class MapBottomSheetFragment  : BottomSheetDialogFragment()  {

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemSelected(lat_value: String, longi_value: String, location: String)
    }

    var lat: String = ""
    var longi: String = ""
    var initialLocation: String = ""
    var initialType: String = ""

    // Method to set initial data
    fun setInitialData(latss: String, longss: String, locationss: String, typess: String) {
        lat = latss
        longi = longss
        initialLocation = locationss
        initialType = typess
    }

    private lateinit var mapView: MapView
    private var map: GoogleMap? = null
    private lateinit var currentLocation: LatLng
    private var marker: Marker? = null

    //location
    private lateinit var searchlocationEdit: EditText
    private lateinit var listViews: ListView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: ArrayAdapter<String>
    private val suggestionsList = mutableListOf<String>()
    private val placeIdList = mutableListOf<String>()

    private lateinit var cardCancel: CardView
    private lateinit var cardSubmit: CardView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_map_bottom_sheet, container, false)

        mapView = view.findViewById(R.id.map_view)

        //search
        searchlocationEdit = view.findViewById(R.id.searchlocationEdit)
        listViews = view.findViewById(R.id.listViews)
        cardCancel = view.findViewById(R.id.cardCancel)
        cardSubmit = view.findViewById(R.id.cardSubmit)

        cardCancel.setOnClickListener {
            dismiss()
        }
        cardSubmit.setOnClickListener {
            if (lat.equals("")){
                Toast.makeText(requireActivity(),"select your location", Toast.LENGTH_SHORT).show()
            }else{
                listener?.onItemSelected(lat, longi, searchlocationEdit.text?.trim().toString())
                dismiss()
            }
        }

        location()
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED // Set initial state to expanded
                behavior.isDraggable = false // Disable dragging
            }
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            map = googleMap
            setupMap(37.7749, -122.4194)

            // Set a map click listener to change the marker's location
            map?.setOnMapClickListener { latLng ->
                updateMarkerLocation(latLng)
            }
        }
    }

    private fun setupMap(latitude: Double, longitude: Double) {
        map?.clear()
        currentLocation = LatLng(latitude, longitude)
        marker = map?.addMarker(MarkerOptions().position(currentLocation).title("You are here")) // Initialize marker
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
    }

    private fun updateMarkerLocation(latLng: LatLng) {
        marker?.let {
            it.position = latLng
            currentLocation = latLng
            setupMap(latLng.latitude, latLng.longitude)
            //set lat long
            lat = latLng.latitude.toString()
            longi = latLng.longitude.toString()
        }
    }

    //search location
    private fun location() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), RetrofitClient.MapKey)
        }
        placesClient = Places.createClient(requireActivity())
        adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, suggestionsList)
        listViews.adapter = adapter

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchPlace(query)
                }else{
                    listViews.visibility = View.GONE
                }
            }
        }

        searchlocationEdit.addTextChangedListener(textWatcher)
        listViews.setOnItemClickListener { parent, view, position, id ->
            val selectedPlaceId = placeIdList[position]
            val selectedPlaceName = suggestionsList[position]
            searchlocationEdit.removeTextChangedListener(textWatcher)
            searchlocationEdit.setText(selectedPlaceName)
            searchlocationEdit.addTextChangedListener(textWatcher)

            listViews.visibility = View.GONE

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
                    listViews.visibility = View.VISIBLE
                    adapter.notifyDataSetChanged()
                } else {
                    listViews.visibility = View.GONE
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
                    updateMarkerLocation(latLng)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapsActivity", "Place details fetch failed: $exception")
            }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    //set the listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

}