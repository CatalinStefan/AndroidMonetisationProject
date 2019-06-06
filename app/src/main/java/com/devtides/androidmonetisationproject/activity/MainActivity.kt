package com.devtides.androidmonetisationproject.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.*
import com.devtides.androidmonetisationproject.BuildConfig
import com.devtides.androidmonetisationproject.R
import com.devtides.androidmonetisationproject.adapter.CountryClickListener
import com.devtides.androidmonetisationproject.adapter.CountryListAdapter
import com.devtides.androidmonetisationproject.model.BannerAd
import com.devtides.androidmonetisationproject.model.Country
import com.devtides.androidmonetisationproject.model.ListItem
import com.devtides.androidmonetisationproject.presenter.CountriesPresenter
import com.devtides.androidmonetisationproject.util.BillingAgent
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CountriesPresenter.View, CountryClickListener, BillingCallback {

    private var controller = CountriesPresenter(this)
    private val countriesList = ArrayList<ListItem>()
    private var countriesAdapter = CountryListAdapter(arrayListOf(), this)
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private var clickedCountry: Country? = null
    private var billingAgent: BillingAgent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(BuildConfig.FLAVOR == "free") {
            MobileAds.initialize(this, "ca-app-pub-9057526686789846~8838914091")
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = countriesAdapter
        }

        billingAgent = BillingAgent(this, this)
    }

    override fun onDestroy() {
        billingAgent?.onDestroy()
        billingAgent = null
        super.onDestroy()
    }

    override fun setCountries(countries: List<Country>) {
        countriesList.clear()
        if(BuildConfig.FLAVOR == "free") {
            var i = 0
            for (country in countries) {
                i++
                if (i % 10 == 0) {
                    countriesList.add(BannerAd())
                } else {
                    countriesList.add(country)
                }
            }
        } else {
            countriesList.addAll(countries)
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
//        if(BuildConfig.FLAVOR == "free") {
//            clickedCountry = country
//            progress!!.visibility = View.VISIBLE
//            list!!.visibility = View.GONE
//            retryButton!!.visibility = View.GONE
//            showRewardedAd()
//        } else {
//            startActivity(DetailActivity.getIntent(this@MainActivity, country))
//        }

        clickedCountry = country
//        billingAgent?.purchaseView()
        billingAgent?.purchaseSubscription()
    }

    override fun onTokenConsumed() {
        startActivity(DetailActivity.getIntent(this@MainActivity, clickedCountry))
    }

    private fun showRewardedAd() {
        val listener = object: RewardedVideoAdListener {
            override fun onRewardedVideoAdClosed() {
                showList()
            }

            override fun onRewardedVideoAdLeftApplication() {
                showList()
            }

            override fun onRewardedVideoAdLoaded() {
                mRewardedVideoAd.show()
            }

            override fun onRewardedVideoAdOpened() {
            }

            override fun onRewardedVideoCompleted() {
                showList()
            }

            override fun onRewarded(p0: RewardItem?) {
                mRewardedVideoAd.destroy(this@MainActivity)
                startActivity(DetailActivity.getIntent(this@MainActivity, clickedCountry))
            }

            override fun onRewardedVideoStarted() {
            }

            override fun onRewardedVideoAdFailedToLoad(p0: Int) {
                mRewardedVideoAd.destroy(this@MainActivity)
                showList()
                startActivity(DetailActivity.getIntent(this@MainActivity, clickedCountry))
            }
        }

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = listener
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
            AdRequest.Builder().build())
    }

    private fun showList() {
        progress!!.visibility = View.GONE
        list!!.visibility = View.VISIBLE
        retryButton!!.visibility = View.GONE
    }

    fun onRetry(v: View) {
        progress!!.visibility = View.VISIBLE
        list!!.visibility = View.GONE
        retryButton!!.visibility = View.GONE
        controller.onRetry()
    }
}
