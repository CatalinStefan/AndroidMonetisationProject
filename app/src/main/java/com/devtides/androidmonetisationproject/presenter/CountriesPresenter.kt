package com.devtides.androidmonetisationproject.presenter

import com.devtides.androidmonetisationproject.model.CountriesService
import com.devtides.androidmonetisationproject.model.Country
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CountriesPresenter(val view: View) {

    private var service = CountriesService()

    init {
        fetchCountries()
    }

    private fun fetchCountries() {
        service.getCountries()!!
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Country>>() {
                override fun onSuccess(value: List<Country>) {
                    view.setCountries(value)
                }

                override fun onError(e: Throwable) {
                    view.onError()
                }
            })
    }

    fun onRetry() {
        fetchCountries()
    }

    interface View {
        fun setCountries(countries: List<Country>)
        fun onError()
    }
}