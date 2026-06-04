package com.latribu.listadc.common.repositories.meal

class MealRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllMeals(installationId: String, isBreakfast: Int) =
        apiHelper.getAllMeals(installationId, isBreakfast)

    suspend fun getMeal(mealId: Int, installationId: String) =
        apiHelper.getMeal(mealId, installationId)

    suspend fun checkMeal(mealId: Int, isChecked: Int, isLunch: Int, isBreakfast: Int, authorId: Int, installationId: String) =
        apiHelper.checkMeal(mealId, isChecked, isLunch, isBreakfast, authorId, installationId)

    suspend fun addMeal(name: String, isLunch: Int, isBreakfast: Int, authorId: Int, installationId: String) =
        apiHelper.addMeal(name, isLunch, isBreakfast, authorId, installationId)

    suspend fun editMeal(mealId: Int, name: String, isLunch: Int, isBreakfast: Int, authorId: Int, installationId: String) =
        apiHelper.editMeal(mealId, name, isLunch, isBreakfast, authorId, installationId)

    suspend fun saveMealIngredients(mealId: Int, ingredients: String, installationId: String) =
        apiHelper.saveMealIngredients(mealId, ingredients, installationId)

    suspend fun deleteMeal(mealId: Int, authorId: Int, installationId: String) =
        apiHelper.deleteMeal(mealId, authorId, installationId)
}