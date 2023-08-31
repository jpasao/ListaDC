package com.latribu.listadc.common.models

import java.io.Serializable

data class Meal(
    val mealId: Int,
    val name: String,
    val isLunch: Int,
    val isChecked: Int
) : Serializable