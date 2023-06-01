package com.latribu.listadc.common.models

import androidx.collection.SimpleArrayMap

class NotificationModel(remoteMessageData: SimpleArrayMap<String, String>)
{
    val operation: String
    val product: ProductItem
   init {
       this.operation = remoteMessageData.valueAt(5)
       this.product = ProductItem(
           id = remoteMessageData.valueAt(1).toIntOrNull(),
           name = remoteMessageData.valueAt(2),
           isChecked = remoteMessageData.valueAt(3),
           comment = remoteMessageData.valueAt(4),
           quantity = remoteMessageData.valueAt(0).toIntOrNull()
       )
   }
}
