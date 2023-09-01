package com.latribu.listadc.common.repositories.meal

class RestApiHelper (private val apiInterface: ApiInterface) {
    suspend fun getAllMeals(installationId: String) =
        apiInterface.getAllMeals(installationId)

    suspend fun getMeal(mealId: Int, installationId: String) =
        apiInterface.getMeal(mealId, installationId)

    suspend fun checkMeal(mealId: Int, isChecked: Int, authorId: Int, installationId: String) =
        apiInterface.checkMeal(mealId, isChecked, authorId, installationId)

    suspend fun addMeal(name: String, isLunch: Int, authorId: Int) =
        apiInterface.addMeal(name, isLunch, authorId)

    suspend fun editMeal(mealId: Int, name: String, isLunch: Int, authorId: Int) =
        apiInterface.editMeal(mealId, name, isLunch, authorId)

    suspend fun saveMealIngredients(mealId: Int, ingredients: String) =
        apiInterface.saveMealIngredients(mealId, ingredients)
}