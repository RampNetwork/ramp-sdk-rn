package network.ramp.reactnativesdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import network.ramp.sdk.events.model.Asset
import network.ramp.sdk.events.model.Crypto
import network.ramp.sdk.events.model.Fiat
import network.ramp.sdk.events.model.OfframpSale
import network.ramp.sdk.events.model.Purchase
import network.ramp.sdk.facade.Config
import network.ramp.sdk.facade.Flow
import kotlin.collections.forEach


object RampModel {

  const val moduleName = "RampSdk"

  enum class Event(val eventName: String) {
    ON_PURCHASE_CREATED("onPurchaseCreated"),
    ON_WIDGET_CLOSE("onRampDidClose"),
    OFFRAMP_SEND_CRYPTO("offrampSendCrypto"),
    ON_OFFRAMP_SALE_CREATED("onOfframpSaleCreated")
  }

  fun getConfig(rawConfig: ReadableMap) = Config(
    url = rawConfig.getString("url").orEmpty(),
    hostAppName = rawConfig.getString("hostAppName").orEmpty(),
    hostLogoUrl = rawConfig.getString("hostLogoUrl").orEmpty(),
    swapAsset = rawConfig.getString("swapAsset").orEmpty(),
    offrampAsset = rawConfig.getString("offrampAsset").orEmpty(),
    swapAmount = rawConfig.getString("swapAmount").orEmpty(),
    fiatCurrency = rawConfig.getString("fiatCurrency").orEmpty(),
    fiatValue = rawConfig.getString("fiatValue").orEmpty(),
    userAddress = rawConfig.getString("userAddress").orEmpty(),
    userEmailAddress = rawConfig.getString("userEmailAddress").orEmpty(),
    selectedCountryCode = rawConfig.getString("selectedCountryCode").orEmpty(),
    defaultAsset = rawConfig.getString("defaultAsset").orEmpty(),
    webhookStatusUrl = rawConfig.getString("webhookStatusUrl").orEmpty(),
    hostApiKey = rawConfig.getString("hostApiKey").orEmpty(),
    defaultFlow = getFlow(rawConfig.getString("defaultFlow")),
    enabledFlows = getEnabledFlows(rawConfig.getArray("enabledFlows")),
    offrampWebhookV3Url = rawConfig.getString("offrampWebhookV3Url").orEmpty(),
    useSendCryptoCallback = getUseSendCryptoCallback(rawConfig)
  )


  private fun getUseSendCryptoCallback(rawConfig: ReadableMap) = try {
    rawConfig.getBoolean("useSendCryptoCallback")
  } catch (exception: Exception) {
    null
  }

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

  private fun getAssetMap(asset: Asset): WritableMap {
    val assetMap: WritableMap = Arguments.createMap()
    assetMap.putString("address", asset.address)
    assetMap.putString("symbol", asset.symbol)
    assetMap.putString("type", asset.type)
    assetMap.putString("name", asset.name)
    assetMap.putInt("decimals", asset.decimals.toInt())
    assetMap.putString("apiV3Symbol", asset.apiV3Symbol)
    assetMap.putString("apiV3Type", asset.apiV3Type)
    assetMap.putString("chain", asset.chain)

    return assetMap
  }

  private fun getPurchaseMap(purchase: Purchase): WritableMap {
    val purchaseMap: WritableMap = Arguments.createMap()
    purchaseMap.putString("id", purchase.id)
    purchaseMap.putString("endTime", purchase.endTime)
    purchaseMap.putMap("asset", getAssetMap(purchase.asset))
    purchaseMap.putString("receiverAddress", purchase.receiverAddress)
    purchaseMap.putString("cryptoAmount", purchase.cryptoAmount)
    purchaseMap.putString("fiatCurrency", purchase.fiatCurrency)
    purchaseMap.putDouble("fiatValue", purchase.fiatValue.toDouble())
    purchaseMap.putDouble("assetExchangeRate", purchase.assetExchangeRate)
    purchaseMap.putDouble("baseRampFee", purchase.baseRampFee)
    purchaseMap.putDouble("networkFee", purchase.networkFee)
    purchaseMap.putDouble("appliedFee", purchase.appliedFee)
    purchaseMap.putString("paymentMethodType", purchase.paymentMethodType)
    purchase.finalTxHash?.let{
        purchaseMap.putString("finalTxHash", it)
    }
    purchaseMap.putString("createdAt", purchase.createdAt)
    purchaseMap.putString("updatedAt", purchase.updatedAt)
    purchaseMap.putString("status", purchase.status)
    return purchaseMap
  }

  fun getOnOfframpSaleCreatedPayloadMap(
    sale: OfframpSale,
    saleViewToken: String,
    apiUrl: String,
    instanceId: String?
  ): WritableMap {
    val payloadMap: WritableMap = Arguments.createMap()
    payloadMap.putString("instanceId", instanceId)
    payloadMap.putMap("sale", getOfframpSaleMap(sale))
    payloadMap.putString("saleViewToken", saleViewToken)
    payloadMap.putString("apiUrl", apiUrl)
    return payloadMap
  }

  fun getOnOfframpSendCryptoPayloadMap(
    assetInfo: Asset,
    amount: String,
    address: String,
    instanceId: String?
  ): WritableMap {
    val payloadMap: WritableMap = Arguments.createMap()
    payloadMap.putString("instanceId", instanceId)
    payloadMap.putMap("assetInfo", getAssetMap(assetInfo))
    payloadMap.putString("amount", amount)
    payloadMap.putString("address", address)
    return payloadMap
  }

  private fun getOfframpSaleMap(sale: OfframpSale): WritableMap {
    val purchaseMap: WritableMap = Arguments.createMap()
    purchaseMap.putString("id", sale.id)
    purchaseMap.putString("createdAt", sale.createdAt)
    purchaseMap.putMap("crypto", getCrypto(sale.crypto))
    purchaseMap.putMap("fiat", getFiat(sale.fiat))

    return purchaseMap
  }

  private fun getCrypto(crypto: Crypto): WritableMap {
    val cryptoMap: WritableMap = Arguments.createMap()
    cryptoMap.putString("amount", crypto.amount)
    cryptoMap.putMap("assetInfo", getAssetMap(crypto.assetInfo))

    return cryptoMap
  }

  private fun getFiat(fiat: Fiat): WritableMap {
    val fiatMap: WritableMap = Arguments.createMap()
    fiatMap.putDouble("amount", fiat.amount)
    fiatMap.putString("currencySymbol", fiat.currencySymbol)

    return fiatMap
  }

  private fun getFlow(name: String?) =
    when (name?.toUpperCase()) {
      Flow.OFFRAMP.name -> Flow.OFFRAMP
      else -> Flow.ONRAMP
    }

  private fun getEnabledFlows(arrayOfFlows: ReadableArray?): Set<Flow> {
    val mutableSet = mutableSetOf<Flow>()
    arrayOfFlows?.let {
      ArrayUtil.toArray(it).forEach { value ->
        (value as? String)?.let {
          mutableSet.add(getFlow(value))
        }
      }
    }
    return mutableSet
  }
}