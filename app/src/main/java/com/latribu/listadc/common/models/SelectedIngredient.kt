package com.latribu.listadc.common.models

import java.io.Serializable

data class SelectedIngredient(
    val mealId: Int,
    val name: String,
    val ingredientId: Int
): Serializable