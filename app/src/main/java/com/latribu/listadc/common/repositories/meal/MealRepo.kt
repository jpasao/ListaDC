package com.latribu.listadc.common.repositories.meal

class MealRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllMeals(installationId: String) =
        apiHelper.getAllMeals(installationId)

    suspend fun getMeal(mealId: Int, installationId: String) =
        apiHelper.getMeal(mealId, installationId)

    suspend fun checkMeal(mealId: Int, isChecked: Int, authorId: Int, installationId: String) =
        apiHelper.checkMeal(mealId, isChecked, authorId, installationId)

    suspend fun addMeal(name: String, isLunch: Int, authorId: Int, installationId: String) =
        apiHelper.addMeal(name, isLunch, authorId, installationId)

    suspend fun editMeal(mealId: Int, name: String, isLunch: Int, authorId: Int, installationId: String) =
        apiHelper.editMeal(mealId, name, isLunch, authorId, installationId)

    suspend fun saveMealIngredients(mealId: Int, ingredients: String, installationId: String) =
        apiHelper.saveMealIngredients(mealId, ingredients, installationId)
}