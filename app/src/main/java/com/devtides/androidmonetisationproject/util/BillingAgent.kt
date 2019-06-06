package com.devtides.androidmonetisationproject.util

import android.app.Activity
import com.android.billingclient.api.*
import com.devtides.androidmonetisationproject.activity.BillingCallback
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams


class BillingAgent(val activity: Activity, val callback: BillingCallback) : PurchasesUpdatedListener {

    private var billingClient = BillingClient.newBuilder(activity).setListener(this).enablePendingPurchases().build()
    //    private val productsSkuList = listOf("purchase_country_view")
    private val productsSkuList = listOf("purchase_country_view")
    private val subscriptionsSkuList = listOf("unlimited_country_views")
    private val productsList = arrayListOf<SkuDetails>()
    private val subscriptionsList = arrayListOf<SkuDetails>()

    init {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    getAvailableProducts()
                    getAvailableSubscriptions()
                }
            }
        })
    }

    fun onDestroy() {
        billingClient.endConnection()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        checkSubscription(billingResult, purchases)
//        checkProduct(billingResult, purchases)
    }

    private fun checkSubscription(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        if(billingResult?.responseCode == BillingClient.BillingResponseCode.OK ||
            billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            callback.onTokenConsumed()
        }
    }

    private fun checkProduct(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        purchases?.let {
            var token: String? = null
            if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchases.size > 0) {
                token = purchases.get(0).purchaseToken
            } else if (billingResult?.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                val purchasesList = billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList
                if(purchasesList.size > 0) {
                    token = purchasesList[0].purchaseToken
                }
            }

            token?.let {
                val params = ConsumeParams
                    .newBuilder()
                    .setPurchaseToken(token)
                    .setDeveloperPayload("Token consumed")
                    .build()
                billingClient.consumeAsync(params) { billingResult, purchaseToken ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        callback.onTokenConsumed()
                    }
                }
            }
        }
    }

    private fun getAvailableProducts() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(productsSkuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()
            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productsList.clear()
                    productsList.addAll(skuDetailsList)
                }
            }
        }
    }

    private fun getAvailableSubscriptions() {
        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(subscriptionsSkuList)
                .setType(BillingClient.SkuType.SUBS)
                .build()
            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    subscriptionsList.clear()
                    subscriptionsList.addAll(skuDetailsList)
                }
            }
        }
    }

    fun purchaseView() {
        if (productsList.size > 0) {
            val billingFlowParams = BillingFlowParams
                .newBuilder()
                .setSkuDetails(productsList[0])
                .build()
            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

    fun purchaseSubscription() {
        val list = billingClient.queryPurchases(BillingClient.SkuType.SUBS).purchasesList
        if(list.size > 0) {
            callback.onTokenConsumed()
        } else {
            if (subscriptionsList.size > 0) {
                val billingFlowParams = BillingFlowParams
                    .newBuilder()
                    .setSkuDetails(subscriptionsList[0])
                    .build()
                billingClient.launchBillingFlow(activity, billingFlowParams)
            }
        }
    }
}