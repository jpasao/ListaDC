package com.latribu.listadc.common.repositories.meal

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.models.Meal
import com.latribu.listadc.common.models.SelectedIngredient
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiInterface {

    @GET(Constants.MEAL_ENDPOINT)
    suspend fun getAllMeals(
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : List<Meal>

    @GET(Constants.MEAL_ENDPOINT)
    suspend fun getMeal(
        @Query("mealId") mealId: Int,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : List<SelectedIngredient>?

    @FormUrlEncoded
    @PATCH(Constants.MEAL_ENDPOINT)
    suspend fun checkMeal(
        @Field("mealId") mealId: Int,
        @Field("check") isChecked: Int,
        @Field("authorId") authorId: Int,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : List<Meal>

    @FormUrlEncoded
    @POST(Constants.MEAL_ENDPOINT)
    suspend fun addMeal(
        @Field("name") name: String,
        @Field("isLunch") isLunch: Int,
        @Field("authorId") authorId: Int,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : Meal

    @FormUrlEncoded
    @PUT(Constants.MEAL_ENDPOINT)
    suspend fun editMeal(
        @Field("mealId") mealId: Int,
        @Field("name") name: String,
        @Field("isLunch") isLunch: Int,
        @Field("authorId") authorId: Int,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : Meal

    @FormUrlEncoded
    @PUT(Constants.MEAL_ENDPOINT)
    suspend fun saveMealIngredients(
        @Field("mealId") mealId: Int,
        @Field("ingredients") ingredients: String,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : List<SelectedIngredient>
}