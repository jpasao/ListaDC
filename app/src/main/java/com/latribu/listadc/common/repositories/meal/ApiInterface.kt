package com.latribu.listadc.common.repositories.meal

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.models.Meal
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiInterface {

    @GET(Constants.MEAL_ENDPOINT)
    suspend fun getAllMeals(
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : List<Meal>

    @FormUrlEncoded
    @PATCH(Constants.MEAL_ENDPOINT)
    suspend fun checkMeal(
        @Field("mealId") mealId: Int,
        @Field("check") isChecked: Int,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : List<Meal>

    @FormUrlEncoded
    @POST(Constants.MEAL_ENDPOINT)
    suspend fun addMeal(
        @Field("name") name: String,
        @Field("isLunch") isLunch: Int
    ) : Meal

    @FormUrlEncoded
    @PUT(Constants.MEAL_ENDPOINT)
    suspend fun editMeal(
        @Field("mealId") mealId: Int,
        @Field("name") name: String,
        @Field("isLunch") isLunch: Int
    ) : Meal
}