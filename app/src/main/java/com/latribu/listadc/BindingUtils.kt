package com.latribu.listadc

import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.latribu.listadc.models.ProductItem

fun isChecked(item: ProductItem?): Boolean = item?.isChecked == "1"

@BindingAdapter("setText")
fun TextView.setText(item: ProductItem?) {
    item?.let {
        text = item.name
        val transparency = if (isChecked(item)) 0.54f else 0.87f
        alpha = transparency
    }
}

@BindingAdapter("setQuantity")
fun Button.setText(item: ProductItem?) {
    item?.let { text = item.quantity.toString() }
}

@BindingAdapter("setChecked")
fun CheckBox.setChecked(item: ProductItem?) {
    isChecked = isChecked(item)
}