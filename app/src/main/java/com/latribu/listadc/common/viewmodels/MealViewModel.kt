package com.latribu.listadc.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.latribu.listadc.common.network.Resource
import com.latribu.listadc.common.repositories.meal.MealRepo

class MealViewModel(private val mMealRepo: MealRepo) : ViewModel() {
    fun getAllMeals(installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.getAllMeals(installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun getMeal(mealId: Int, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.getMeal(mealId, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun checkMeal(mealId: Int, isChecked: Int, isLunch: Int, authorId: Int, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.checkMeal(mealId, isChecked, isLunch, authorId, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun addMeal(name: String, isLunch: Int, authorId: Int, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.addMeal(name, isLunch, authorId, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun editMeal(mealId: Int, name: String, isLunch: Int, authorId: Int, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.editMeal(mealId, name, isLunch, authorId, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun saveMealIngredients(mealId: Int, ingredients: String, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.saveMealIngredients(mealId, ingredients, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}