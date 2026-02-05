package com.royalit.garghi.AdaptersAndModels

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.royalit.garghi.AdaptersAndModels.Categorys.SubCategoriesModel
import com.royalit.garghi.R

class SubCatListAdapter (
    context: Context,
    private val categoriesModel: List<SubCategoriesModel>
) : ArrayAdapter<SubCategoriesModel>(context, R.layout.spinner_item, categoriesModel) {

    init {
        setDropDownViewResource(R.layout.spinner_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinnerItemText)
        textView.text = categoriesModel[position].subcategory
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false)
        val textView = view.findViewById<TextView>(R.id.spinnerItemText)
        textView.text = categoriesModel[position].subcategory
        return view
    }
}