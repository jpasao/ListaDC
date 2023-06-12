package com.latribu.listadc.common.models

import android.content.Intent

class FirebaseData(intent: Intent?) {
    val operation: String
    val product: ProductItem
    val user: Int?
    init {
        this.operation = intent!!.getStringExtra("operation").toString()
        this.product = ProductItem(
            id = intent!!.getStringExtra("productId")?.toIntOrNull(),
            name = intent!!.getStringExtra("name").toString(),
            isChecked = intent!!.getStringExtra("isChecked").toString(),
            comment = intent!!.getStringExtra("comment").toString(),
            quantity = intent!!.getStringExtra("quantity")?.toIntOrNull()
        )
        this.user = intent!!.getStringExtra("user")?.toIntOrNull()
    }
}
