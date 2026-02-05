package com.royalit.garghi.AdaptersAndModels.Citys

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.royalit.garghi.R


class CityAdapter(
    context: Context,
    private val cities: List<CitysModel>
) : ArrayAdapter<CitysModel>(context, R.layout.spinner_item, cities) {

    init {
        // Define the layout to use for the Spinner dropdown
        setDropDownViewResource(R.layout.spinner_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinnerItemText)
        textView.text = cities[position].city
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinnerItemText)
        textView.text = cities[position].city
        return view
    }
}