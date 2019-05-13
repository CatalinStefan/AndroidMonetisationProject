package com.devtides.androidmonetisationproject.model

import io.reactivex.Single
import retrofit2.http.GET

interface CountriesApi {
    @GET("countriesV2.json")
    abstract fun getCountries(): Single<List<Country>>
}