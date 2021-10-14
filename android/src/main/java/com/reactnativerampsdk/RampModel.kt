package com.reactnativerampsdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import network.ramp.sdk.facade.Config
import network.ramp.sdk.events.model.Purchase


object RampModel {

    const val moduleName ="RampSdk"

    enum class Event(val eventName: String) {
        ON_PURCHASE_CREATED("onRamp"),
        ON_PURCHASE_FAILED("onRampPurchaseDidFail"),
        ON_WIDGET_CLOSE("onRampDidClose")
    }

    fun getConfig(rawConfig: ReadableMap) = Config(
        url = rawConfig.getString("url").orEmpty(),
        hostAppName = rawConfig.getString("hostAppName").orEmpty(),
        hostLogoUrl = rawConfig.getString("hostLogoUrl").orEmpty(),
        swapAsset = rawConfig.getString("swapAsset").orEmpty(),
        swapAmount = rawConfig.getString("swapAmount").orEmpty(),
        fiatCurrency = rawConfig.getString("fiatCurrency").orEmpty(),
        fiatValue = rawConfig.getString("fiatValue").orEmpty(),
        userAddress = rawConfig.getString("userAddress").orEmpty(),
        userEmailAddress = rawConfig.getString("userEmailAddress").orEmpty(),
        selectedCountryCode = rawConfig.getString("selectedCountryCode").orEmpty(),
        defaultAsset = rawConfig.getString("defaultAsset").orEmpty(),
        webhookStatusUrl = rawConfig.getString("webhookStatusUrl").orEmpty(),
        hostApiKey = rawConfig.getString("hostApiKey").orEmpty()
    )

    fun getInstance(rawConfig: ReadableMap) = rawConfig.getString("instanceId")

    fun getOnPurchaseCreatedPayloadMap(
        purchase: Purchase,
        purchaseViewToken: String,
        apiUrl: String,
        instanceId: String?
    ): WritableMap {
        val payloadMap: WritableMap = Arguments.createMap()
        payloadMap.putString("instanceId", instanceId)
        payloadMap.putMap("purchase", getPurchaseMap(purchase))
        payloadMap.putString("purchaseViewToken", purchaseViewToken)
        payloadMap.putString("apiUrl", apiUrl)
        return payloadMap
    }

    fun getInstanceMap(instanceId: String?): WritableMap {
        val params: WritableMap = Arguments.createMap()
        params.putString("instanceId", instanceId)
        return params
    }

    private fun getAssetMap(purchase: Purchase): WritableMap {
        val assetMap: WritableMap = Arguments.createMap()
        assetMap.putString("address", purchase.asset.address)
        assetMap.putString("symbol", purchase.asset.symbol)
        assetMap.putString("type", purchase.asset.type)
        assetMap.putString("name", purchase.asset.name)
        assetMap.putInt("decimals", purchase.asset.decimals.toInt())

        return assetMap
    }

    private fun getPurchaseMap(purchase: Purchase): WritableMap {
        val purchaseMap: WritableMap = Arguments.createMap()
        purchaseMap.putString("id", purchase.id)
        purchaseMap.putString("endTime", purchase.endTime)
        purchaseMap.putMap("asset", getAssetMap(purchase))
        purchaseMap.putString("receiverAddress", purchase.receiverAddress)
        purchaseMap.putString("cryptoAmount", purchase.cryptoAmount)
        purchaseMap.putString("fiatCurrency", purchase.fiatCurrency)
        purchaseMap.putString("fiatValue", purchase.fiatValue.toString()) // Double?
        purchaseMap.putDouble("assetExchangeRate", purchase.assetExchangeRate)
        purchaseMap.putDouble("baseRampFee", purchase.baseRampFee)
        purchaseMap.putDouble("networkFee", purchase.networkFee)
        purchaseMap.putDouble("appliedFee", purchase.appliedFee)
        purchaseMap.putString("paymentMethodType", purchase.paymentMethodType)
        // purchaseMap.putString("finalTxHash", purchase.finalTxHash) // ToDo Missing?
        purchaseMap.putString("createdAt", purchase.createdAt)
        purchaseMap.putString("updatedAt", purchase.updatedAt)
        purchaseMap.putString("status", purchase.status)
        purchaseMap.putString("escrowAddress", purchase.escrowAddress)
        // purchaseMap.putString("escrowDetailsHash", purchase.escrowDetailsHash // ToDo Missing?

        return purchaseMap
    }
}