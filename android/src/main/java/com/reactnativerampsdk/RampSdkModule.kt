package com.reactnativerampsdk

import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

import network.ramp.sdk.events.model.Purchase
import network.ramp.sdk.facade.RampCallback
import network.ramp.sdk.facade.RampSDK
import com.facebook.react.bridge.ReadableMap

import com.facebook.react.bridge.WritableMap

import com.facebook.react.bridge.ReactContext
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import javax.annotation.Nullable


class RampSdkModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {
    private var instanceId: String? = null

    private val rampSDK = RampSDK()

    override fun getName(): String {
        return "RampSdk"
    }

    @ReactMethod
    fun runRamp(rawConfig: ReadableMap) {

        //configJson parse from json to Config object
        val config = Model.getConfig(rawConfig)

        instanceId = rawConfig.getString("instanceId")

        val callbackRamp: RampCallback = object : RampCallback {
            override fun onPurchaseCreated(
                purchase: Purchase,
                purchaseViewToken: String,
                apiUrl: String
            ) {
                sendEvent(
                    reactApplicationContext,
                    "onRamp",
                    Model.getPurchase(purchase, purchaseViewToken, apiUrl, instanceId)
                )
            }

            override fun onPurchaseFailed() {
                sendEvent(
                    reactApplicationContext,
                    "onRampPurchaseDidFail",
                    Model.getInstance(instanceId)
                )
            }


            override fun onWidgetClose() {
                sendEvent(
                    reactApplicationContext,
                    "onRampDidClose",
                    Model.getInstance(instanceId)
                )
            }
        }

        this.currentActivity?.let{ activity ->
            rampSDK.startTransaction(activity, config, callbackRamp)
        }?: { Log.e("RampSdkModule","Current Activity is null.")}

    }

    private fun sendEvent(
        reactContext: ReactContext,
        eventName: String,
        @Nullable params: WritableMap
    ) {
        reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
    }
}
