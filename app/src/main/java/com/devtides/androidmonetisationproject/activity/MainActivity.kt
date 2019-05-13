package com.devtides.androidmonetisationproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.*
import com.devtides.androidmonetisationproject.R
import com.devtides.androidmonetisationproject.adapter.CountryClickListener
import com.devtides.androidmonetisationproject.adapter.CountryListAdapter
import com.devtides.androidmonetisationproject.model.BannerAd
import com.devtides.androidmonetisationproject.model.Country
import com.devtides.androidmonetisationproject.model.ListItem
import com.devtides.androidmonetisationproject.presenter.CountriesPresenter
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CountriesPresenter.View, CountryClickListener {

    private var controller = CountriesPresenter(this)
    private val countriesList = ArrayList<ListItem>()
    private var countriesAdapter = CountryListAdapter(arrayListOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this, "ca-app-pub-9057526686789846~8838914091")

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }
    }

    override fun setCountries(countries: List<Country>) {
        countriesList.clear()
        var i = 0
        for(country in countries) {
            i++
            if(i % 10 == 0) {
                countriesList.add(BannerAd())
            } else {
                countriesList.add(country)
            }
        }
        countriesAdapter.updateCountries(countriesList)
        retryButton.visibility = View.GONE
        progress.visibility = View.GONE
        list.visibility = View.VISIBLE
    }

    override fun onError() {
        Toast.makeText(this, "Unable to get country names. Please retry later.", Toast.LENGTH_SHORT).show()
        progress!!.visibility = View.GONE
        list!!.visibility = View.GONE
        retryButton!!.visibility = View.VISIBLE
    }

    override fun onCountryClick(country: Country) {
        startActivity(DetailActivity.getIntent(this, country))
    }

    fun onRetry(v: View) {
        progress!!.visibility = View.VISIBLE
        list!!.visibility = View.GONE
        retryButton!!.visibility = View.GONE
        controller.onRetry()
    }
}
