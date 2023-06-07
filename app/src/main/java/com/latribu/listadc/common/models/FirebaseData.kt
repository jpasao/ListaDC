package com.latribu.listadc.common.models

import androidx.collection.SimpleArrayMap

class FirebaseData(remoteMessageData: SimpleArrayMap<String, String>)
{
    val operation: String
    val product: ProductItem
    val user: Int?
   init {
       this.operation = remoteMessageData.valueAt(6)
       this.product = ProductItem(
           id = remoteMessageData.valueAt(1).toIntOrNull(),
           name = remoteMessageData.valueAt(2),
           isChecked = remoteMessageData.valueAt(4),
           comment = remoteMessageData.valueAt(5),
           quantity = remoteMessageData.valueAt(0).toIntOrNull()
       )
       this.user = remoteMessageData.valueAt(3).toIntOrNull()
   }
}
