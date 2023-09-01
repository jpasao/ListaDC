package com.latribu.listadc.common.repositories.meal

class MealRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllMeals(installationId: String) =
        apiHelper.getAllMeals(installationId)

    suspend fun getMeal(mealId: Int, installationId: String) =
        apiHelper.getMeal(mealId, installationId)

    suspend fun checkMeal(mealId: Int, isChecked: Int, authorId: Int, installationId: String) =
        apiHelper.checkMeal(mealId, isChecked, authorId, installationId)

    suspend fun addMeal(name: String, isLunch: Int, authorId: Int) =
        apiHelper.addMeal(name, isLunch, authorId)

    suspend fun editMeal(mealId: Int, name: String, isLunch: Int, authorId: Int) =
        apiHelper.editMeal(mealId, name, isLunch, authorId)

    suspend fun saveMealIngredients(mealId: Int, ingredients: String) =
        apiHelper.saveMealIngredients(mealId, ingredients)
}