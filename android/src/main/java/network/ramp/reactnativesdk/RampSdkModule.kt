package network.ramp.reactnativesdk

import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import network.ramp.sdk.events.model.OfframpSale
import network.ramp.sdk.events.model.Purchase
import network.ramp.sdk.events.model.Asset
import network.ramp.sdk.facade.RampCallback
import network.ramp.sdk.facade.RampSDK
import javax.annotation.Nullable


class RampSdkModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), RampCallback {

    private var instanceId: String? = null

    private val rampSDK = RampSDK()

    override fun getName(): String = RampModel.moduleName

    @ReactMethod
    fun runRamp(rawConfig: ReadableMap) {
        val config = RampModel.getConfig(rawConfig)

        println("SDK VERSION: " + network.ramp.sdk.BuildConfig.VERSION)
        println("URL: " + config.url)
        println("CONFIG: " + config)

        instanceId = RampModel.getInstance(rawConfig)

        this.currentActivity?.let { activity ->
            rampSDK.startTransaction(activity, config, this)
        } ?: run { Log.e("RampSdkModule", "Current Activity cannot be null.") }
    }


    @ReactMethod
    fun onOfframpCryptoSent(txHash: String, error: String) {
        this.currentActivity?.let { activity ->
            rampSDK.onOfframpCryptoSent(txHash, error)
        } ?: run { Log.e("RampSdkModule", "Current Activity cannot be null.") }
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        // Keep: Required for RN built in Event Emitter Calls.
    }

    override fun onPurchaseCreated(
        purchase: Purchase,
        purchaseViewToken: String,
        apiUrl: String
    ) {
        sendEvent(
            reactApplicationContext,
            RampModel.Event.ON_PURCHASE_CREATED.eventName,
            RampModel.getOnPurchaseCreatedPayloadMap(
                purchase,
                purchaseViewToken,
                apiUrl,
                instanceId
            )
        )
    }

    override fun onPurchaseFailed() {
        sendEvent(
            reactApplicationContext,
            RampModel.Event.ON_PURCHASE_FAILED.eventName,
            RampModel.getInstanceMap(instanceId)
        )
    }

    override fun onWidgetClose() {
        sendEvent(
            reactApplicationContext,
            RampModel.Event.ON_WIDGET_CLOSE.eventName,
            RampModel.getInstanceMap(instanceId)
        )
    }

    override fun onOfframpSaleCreated(
        sale: OfframpSale,
        saleViewToken: String,
        apiUrl: String
    ) {
        sendEvent(
            reactApplicationContext,
            RampModel.Event.ON_OFFRAMP_SALE_CREATED.eventName,
            RampModel.getOnOfframpSaleCreatedPayloadMap(
                sale,
                saleViewToken,
                apiUrl,
                instanceId
            )
        )
    }

    override fun offrampSendCrypto(
        assetInfo: Asset,
        amount: String,
        address: String
    ) {
        sendEvent(
            reactApplicationContext,
            RampModel.Event.OFFRAMP_SEND_CRYPTO.eventName,
            RampModel.getOnOfframpSendCryptoPayloadMap(assetInfo, amount, address, instanceId)
        )
    }

    private fun sendEvent(
        reactContext: ReactContext,
        eventName: String,
        @Nullable params: WritableMap
    ) {
        reactContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(eventName, params)
    }
}