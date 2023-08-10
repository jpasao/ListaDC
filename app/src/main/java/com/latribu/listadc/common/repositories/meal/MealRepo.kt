package com.latribu.listadc.common.repositories.meal

class MealRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllMeals(installationId: String) =
        apiHelper.getAllMeals(installationId)

    suspend fun checkMeal(mealId: Int, isChecked: Int, installationId: String) =
        apiHelper.checkMeal(mealId, isChecked, installationId)

    suspend fun addMeal(name: String, isLunch: Int) =
        apiHelper.addMeal(name, isLunch)

    suspend fun editMeal(mealId: Int, name: String, isLunch: Int) =
        apiHelper.editMeal(mealId, name, isLunch)
}