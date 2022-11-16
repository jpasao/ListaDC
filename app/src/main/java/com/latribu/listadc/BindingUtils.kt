package com.latribu.listadc

import android.graphics.Color
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.latribu.listadc.models.ProductItem

fun isItemChecked(item: ProductItem?): Boolean = item?.isChecked == "1"

@BindingAdapter("setText")
fun TextView.setText(item: ProductItem?) {
    item?.let {
        text = item.name
        val checked = isItemChecked(item)
        setTextColor(if (checked) Color.LTGRAY else Color.GRAY)
    }
}

@BindingAdapter("setQuantity")
fun Button.setText(item: ProductItem?) {
    item?.let { text = item.quantity.toString() }
}

@BindingAdapter("setChecked")
fun CheckBox.setChecked(item: ProductItem?) {
    isChecked = isItemChecked(item)
}