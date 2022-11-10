package com.latribu.listadc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.latribu.listadc.models.Product

class RecyclerAdapter(private val productList: Product) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.list_item_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productList[position]
        holder.checkBoxView.isChecked = item.checked
        holder.buttonView.text = item.quantity.toString()
        holder.textView.text = item.name
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBoxView: CheckBox = view.findViewById(R.id.check)
        val buttonView: Button = view.findViewById(R.id.quantity)
        val textView: TextView = view.findViewById(R.id.name)
    }
}