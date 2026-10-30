package com.devforgely.beeproductive.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.devforgely.beeproductive.domain.model.OfferLoad
import com.devforgely.beeproductive.domain.model.SupportOffer
import com.devforgely.beeproductive.domain.model.SupportProducts
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

sealed interface BillingEvent {
    data object Canceled : BillingEvent
    data object Purchased : BillingEvent
    data class Failed(val message: String) : BillingEvent
}

@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferences: UserPreferencesRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val offersState = MutableStateFlow<OfferLoad>(OfferLoad.Loading)
    val offers = offersState.asStateFlow()
    private val eventState = MutableSharedFlow<BillingEvent>(extraBufferCapacity = 1)
    val events = eventState.asSharedFlow()

    private var billingClient: BillingClient? = null
    private val details = mutableMapOf<String, ProductDetails>()

    fun connect() {
        if (billingClient != null) return
        val client = BillingClient.newBuilder(context)
            .setListener { result, purchases ->
                scope.launch { onPurchasesUpdated(result, purchases) }
            }
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build(),
            )
            .enableAutoServiceReconnection()
            .build()
        billingClient = client
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    refresh()
                } else {
                    offersState.value = OfferLoad.Unavailable(
                        "Play Billing is not available on this device.",
                    )
                }
            }

            override fun onBillingServiceDisconnected() {
                offersState.value = OfferLoad.Unavailable("Reconnecting to the Play Store…")
            }
        })
    }

    fun refresh() {
        val client = billingClient
        if (client == null || !client.isReady) {
            connect()
            return
        }
        queryProducts()
        queryPurchases(announce = false)
    }

    fun launch(activity: Activity, productId: String): Boolean {
        val client = billingClient ?: return false
        val product = details[productId] ?: return false
        val offerToken = product.oneTimePurchaseOfferDetails?.offerToken
            ?: product.oneTimePurchaseOfferDetailsList?.firstOrNull()?.offerToken
            ?: return false
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(product)
                        .setOfferToken(offerToken)
                        .build(),
                ),
            )
            .build()
        return client.launchBillingFlow(activity, params).responseCode ==
            BillingClient.BillingResponseCode.OK
    }

    private fun queryProducts() {
        val client = billingClient ?: return
        val products = SupportProducts.catalog.map { tier ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(tier.id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(products)
            .build()
        client.queryProductDetailsAsync(params) { result, detailsResult ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                offersState.value = OfferLoad.Unavailable(
                    "Support gifts could not be loaded. Try again in a moment.",
                )
                return@queryProductDetailsAsync
            }
            details.clear()
            detailsResult.productDetailsList.forEach { product ->
                details[product.productId] = product
            }
            val tiers = SupportProducts.catalog.mapNotNull { tier ->
                val product = details[tier.id] ?: return@mapNotNull null
                val price = product.oneTimePurchaseOfferDetails?.formattedPrice
                    ?: product.oneTimePurchaseOfferDetailsList?.firstOrNull()?.formattedPrice
                    ?: return@mapNotNull null
                SupportOffer(
                    productId = tier.id,
                    title = tier.title,
                    blurb = tier.blurb,
                    formattedPrice = price,
                )
            }
            offersState.value = if (tiers.isEmpty()) {
                OfferLoad.Unavailable("Support gifts are not in the Play Store yet.")
            } else {
                OfferLoad.Ready(tiers)
            }
        }
    }

    private fun queryPurchases(announce: Boolean) {
        val client = billingClient ?: return
        client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryPurchasesAsync
            purchases.forEach { consumeSupport(it, announce) }
        }
    }

    private suspend fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases.orEmpty().forEach { consumeSupport(it, announce = true) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> eventState.emit(BillingEvent.Canceled)
            else -> eventState.emit(
                BillingEvent.Failed(
                    result.debugMessage.ifBlank { "The gift could not be sent." },
                ),
            )
        }
    }

    private fun consumeSupport(purchase: Purchase, announce: Boolean) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        val productId = SupportProducts.matching(purchase.products) ?: return
        val client = billingClient ?: return
        val params = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        client.consumeAsync(params) { result, _ ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@consumeAsync
            scope.launch {
                preferences.markSupporter(productId)
                if (announce) eventState.emit(BillingEvent.Purchased)
            }
        }
    }
}
