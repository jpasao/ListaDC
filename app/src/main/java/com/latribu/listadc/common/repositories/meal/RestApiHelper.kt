package com.latribu.listadc.common.repositories.meal

class RestApiHelper (private val apiInterface: ApiInterface) {
    suspend fun getAllMeals(installationId: String) =
        apiInterface.getAllMeals(installationId)

    suspend fun checkMeal(mealId: Int, isChecked: Int, installationId: String) =
        apiInterface.checkMeal(mealId, isChecked, installationId)

    suspend fun addMeal(name: String, isLunch: Int) =
        apiInterface.addMeal(name, isLunch)

    suspend fun editMeal(mealId: Int, name: String, isLunch: Int) =
        apiInterface.editMeal(mealId, name, isLunch)
}