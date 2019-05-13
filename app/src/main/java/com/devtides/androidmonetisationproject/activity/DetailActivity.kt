package com.devtides.androidmonetisationproject.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.devtides.androidmonetisationproject.R
import com.devtides.androidmonetisationproject.model.Country
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    lateinit var country: Country

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if(intent.hasExtra(PARAM_COUNTRY) && intent.getParcelableExtra<Country>(PARAM_COUNTRY) != null) {
            country = intent.getParcelableExtra<Country>(PARAM_COUNTRY)
        } else {
            finish()
        }

        populate()
    }

    fun populate() {
        Glide.with(this)
            .load(country.flag)
            .into(countryFlag)
        textName.text = country.countryName
        textCapital.text = country.capital
        textArea.text = "Area: ${country.area}"
        textRegion.text = "Region: ${country.region}"
    }

    companion object {
        val PARAM_COUNTRY = "country"

        fun getIntent(context: Context, country: Country): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(PARAM_COUNTRY, country)
            return intent
        }
    }
}
