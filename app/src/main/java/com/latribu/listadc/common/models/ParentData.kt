package com.latribu.listadc.common.models

import com.latribu.listadc.common.Constants

data class ParentData<T>(
    val parentTitle: String? = null,
    var type: Int = Constants.PARENT,
    var subList: ArrayList<T>,
    var isExpanded: Boolean = false
)
