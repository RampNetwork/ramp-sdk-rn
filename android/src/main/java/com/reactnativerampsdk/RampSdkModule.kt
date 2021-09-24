package com.reactnativerampsdk

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

import network.ramp.sdk.events.model.Purchase
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.RampCallback
import network.ramp.sdk.facade.RampSDK
import com.facebook.react.bridge.ReadableMap

import com.facebook.react.bridge.WritableMap

import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.bridge.Arguments
import javax.annotation.Nullable


class RampSdkModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
  private var instanceId: String? = null

  override fun getName(): String {
    return "RampSdk"
  }

  @ReactMethod
  fun runRamp(rawConfig: ReadableMap) {

    //configJson parse from json to Config object
    val config = Config(
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

    instanceId = rawConfig.getString("instanceId")

    val rampSDK = RampSDK()

    val callbackRamp: RampCallback = object : RampCallback {
      override fun onPurchaseCreated(purchase: Purchase, purchaseViewToken: String, apiUrl: String) {
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

        sendEvent(reactApplicationContext, "onRamp", payloadMap)
      }

      override fun onPurchaseFailed() {
        val params: WritableMap = Arguments.createMap()
        params.putString("instanceId", instanceId)
        sendEvent(reactApplicationContext, "onRampPurchaseDidFail", params)
      }


      override fun onWidgetClose() {
        val params: WritableMap = Arguments.createMap()
        params.putString("instanceId", instanceId)
        sendEvent(reactApplicationContext, "onRampDidClose", params)
      }
    }

    rampSDK.startTransaction(this.currentActivity!!, config, callbackRamp)
  }

  private fun sendEvent(reactContext: ReactContext, eventName: String, @Nullable params: WritableMap) {
    reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
  }

}
