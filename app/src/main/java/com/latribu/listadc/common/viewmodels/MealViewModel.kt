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

    fun checkMeal(mealId: Int, isChecked: Int, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.checkMeal(mealId, isChecked, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun addMeal(name: String, isLunch: Int) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.addMeal(name, isLunch)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun editMeal(mealId: Int, name: String, isLunch: Int) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.editMeal(mealId, name, isLunch)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun saveMealIngredients(mealId: Int, ingredients: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mMealRepo.saveMealIngredients(mealId, ingredients)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}