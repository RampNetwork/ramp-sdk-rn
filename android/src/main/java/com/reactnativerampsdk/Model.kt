package com.reactnativerampsdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import network.ramp.sdk.facade.Config
import network.ramp.sdk.events.model.Purchase



object Model {

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

    fun getPurchase(
        purchase: Purchase,
        purchaseViewToken: String,
        apiUrl: String,
        instanceId: String?
    ): WritableMap {
        val payloadMap: WritableMap = Arguments.createMap()
        payloadMap.putString("instanceId", instanceId)

        val purchaseMap: WritableMap = Arguments.createMap()
        purchaseMap.putString("id", purchase.id)
        purchaseMap.putString("endTime", purchase.endTime)

        val assetMap: WritableMap = Arguments.createMap()
        assetMap.putString("address", purchase.asset.address)
        assetMap.putString("symbol", purchase.asset.symbol)
        assetMap.putString("type", purchase.asset.type)
        assetMap.putString("name", purchase.asset.name)
        assetMap.putInt("decimals", purchase.asset.decimals.toInt())

        purchaseMap.putMap("asset", assetMap)

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

        payloadMap.putMap("purchase", purchaseMap)
        payloadMap.putString("purchaseViewToken", purchaseViewToken)
        payloadMap.putString("apiUrl", apiUrl)
        return payloadMap
    }

    fun getInstance(instanceId: String?): WritableMap {
        val params: WritableMap = Arguments.createMap()
        params.putString("instanceId", instanceId)
        return params
    }
}