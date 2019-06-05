package com.devtides.androidmonetisationproject.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.devtides.androidmonetisationproject.R
import com.devtides.androidmonetisationproject.model.Country
import com.devtides.androidmonetisationproject.model.ListItem
import com.devtides.androidmonetisationproject.model.TYPE_AD
import com.devtides.androidmonetisationproject.model.TYPE_COUNTRY
import com.devtides.androidmonetisationproject.util.getProgressDrawable
import com.devtides.androidmonetisationproject.util.loadImage
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.ad_row_layout.view.*
import kotlinx.android.synthetic.main.row_layout.view.*

class CountryListAdapter(var countries: ArrayList<ListItem>, val clickListener: CountryClickListener): RecyclerView.Adapter<CountryListAdapter.CountryListViewHolder>() {

    fun updateCountries(newCountries: ArrayList<ListItem>) {
        countries.clear()
        countries.addAll(newCountries)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = countries[position].type

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CountryListViewHolder {
        var viewHolder =
        when (type) {
            TYPE_COUNTRY -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false)
                CountryViewHolder(view, clickListener)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.ad_row_layout, parent, false)
                AdViewHolder(view)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: CountryListViewHolder, position: Int) {
        holder.bind(countries[position])
    }

    override fun getItemCount() = countries.size

    abstract class CountryListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun bind(item: ListItem)
    }

    class CountryViewHolder(view: View, var clickListener: CountryClickListener): CountryListViewHolder(view) {

        val layout = view.layout
        val imageView = view.imageView
        val countryName = view.name
        val countryCapital = view.capital

        override fun bind(item: ListItem) {
            val country = item as Country
            countryName?.text = country.countryName
            countryCapital?.text = country.capital
            imageView.loadImage(country?.flag, getProgressDrawable(imageView.context))
            layout.setOnClickListener { clickListener.onCountryClick(country) }
        }
    }

    class AdViewHolder(view: View): CountryListViewHolder(view) {

        var mAdView = view.adView

        override fun bind(item: ListItem) {
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
        }

    }
}