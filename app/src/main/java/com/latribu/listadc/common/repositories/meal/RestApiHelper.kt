package com.latribu.listadc.common.repositories.meal

class RestApiHelper (private val apiInterface: ApiInterface) {
    suspend fun getAllMeals(installationId: String) =
        apiInterface.getAllMeals(installationId)

    suspend fun getMeal(mealId: Int, installationId: String) =
        apiInterface.getMeal(mealId, installationId)

    suspend fun checkMeal(mealId: Int, isChecked: Int, isLunch: Int, authorId: Int, installationId: String) =
        apiInterface.checkMeal(mealId, isChecked, isLunch, authorId, installationId)

    suspend fun addMeal(name: String, isLunch: Int, authorId: Int, installationId: String) =
        apiInterface.addMeal(name, isLunch, authorId, installationId)

    suspend fun editMeal(mealId: Int, name: String, isLunch: Int, authorId: Int, installationId: String) =
        apiInterface.editMeal(mealId, name, isLunch, authorId, installationId)

    suspend fun saveMealIngredients(mealId: Int, ingredients: String, installationId: String) =
        apiInterface.saveMealIngredients(mealId, ingredients, installationId)
}