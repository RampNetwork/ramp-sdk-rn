import Ramp
import Foundation

@objc(RampSdk)
class RampSdk: RCTEventEmitter {

    @objc static override func requiresMainQueueSetup() -> Bool {
        return true
    }

    private var instanceId: String?

    @objc(runRamp:)
    func runRamp(rawConfig: NSDictionary) -> Void {
        guard let config = rawConfig as? [String: String] else {
            return
        }
        
        let configuration = mapConfig(rawConfig: config)
        instanceId = config["instanceId"]
        
        let ramp = try! RampViewController(configuration: configuration)
        ramp.delegate = self

        let presentedViewController = RCTPresentedViewController()
        
        DispatchQueue.main.async {
            presentedViewController?.present(ramp, animated: true)
        }
    }
    
    //TODO add method response habndler with SendCryptoResultPayload



    override func supportedEvents() -> [String]! {
        return ["onPurchaseCreated", "onRampDidClose", "offrampSendCrypto", "onOfframpSaleCreated"]
    }
    
    private func mapConfig(rawConfig: [String: Any]) -> Configuration {
        var configuration = Configuration()
        
        configuration.swapAsset = rawConfig["swapAsset"] as? String ?? nil
        configuration.swapAmount = rawConfig["swapAmount"] as? String ?? nil
        configuration.fiatCurrency = rawConfig["fiatCurrency"] as? String ?? nil
        configuration.fiatValue = rawConfig["fiatValue"] as? String ?? nil
        configuration.userAddress = rawConfig["userAddress"] as? String ?? nil
        configuration.hostLogoUrl = rawConfig["hostLogoUrl"] as? String ?? nil
        configuration.hostAppName = rawConfig["hostAppName"] as? String ?? nil
        configuration.userEmailAddress = rawConfig["userEmailAddress"] as? String ?? nil
        configuration.selectedCountryCode = rawConfig["selectedCountryCode"] as? String ?? nil
        configuration.defaultAsset = rawConfig["defaultAsset"] as? String ?? nil
        configuration.url = rawConfig["url"] as? String ?? nil
        configuration.webhookStatusUrl = rawConfig["webhookStatusUrl"] as? String ?? nil

        configuration.hostApiKey = rawConfig["hostApiKey"] as? String ?? nil
        configuration.deepLinkScheme = rawConfig["deepLinkScheme"] as? String ?? nil

        return configuration;
    }
}

extension RampSdk: RampDelegate {
    func ramp(_ rampViewController: RampViewController, didCreateOnrampPurchase purchase: OnrampPurchase, purchaseViewToken: String, apiUrl: URL) {
        let data = try! JSONEncoder().encode(purchase)
        let json = try! JSONSerialization.jsonObject(with: data)
        sendEvent(withName: "onPurchaseCreated", body: [
            "instanceId": instanceId!,
            "purchase": json,
            "purchaseViewToken": purchaseViewToken,
            "apiUrl": apiUrl
        ])
    }

    func ramp(_ rampViewController: RampViewController,
              didRequestSendCrypto payload: SendCryptoPayload,
              responseHandler: @escaping (SendCryptoResultPayload) -> Void) {

        let data = try! JSONEncoder().encode(payload)
        let json = try! JSONSerialization.jsonObject(with: data)

        sendEvent(withName: "offrampSendCrypto", body: [
            "instanceId": instanceId!,
            "payload": json
        ])

        // TODO handle responseHandler

    }

    func ramp(_ rampViewController: RampViewController,
            didCreateOfframpSale sale: OfframpSale,
            saleViewToken: String,
            apiUrl: URL) {

        let data = try! JSONEncoder().encode(sale)
        let json = try! JSONSerialization.jsonObject(with: data)

        sendEvent(withName: "onOfframpSaleCreated", body: [
            "instanceId": instanceId!,
            "sale": json,
            "saleViewToken": purchassaleViewTokeneViewToken,
            "apiUrl": apiUrl
        ])

    }
    
    func rampDidClose(_ rampViewController: RampViewController) {
        sendEvent(withName: "onRampDidClose", body: ["instanceId": instanceId!])
    }
}

extension OnrampPurchase: Encodable {
    
    public enum CodeKeys: String, CodingKey {
        case id
        case endTime
        case asset
        case receiverAddress
        case cryptoAmount
        case fiatCurrency
        case fiatValue
        case assetExchangeRate
        case baseRampFee
        case networkFee
        case appliedFee
        case paymentMethodType
        case finalTxHash
        case createdAt
        case updatedAt
        case status
        case escrowAddress
        case escrowDetailsHash
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodeKeys.self)
        try container.encode (id, forKey: .id)
        try container.encode (endTime, forKey: .endTime)
        try container.encode (asset, forKey: .asset)
        try container.encode (receiverAddress, forKey: .receiverAddress)
        try container.encode (cryptoAmount, forKey: .cryptoAmount)
        try container.encode (fiatCurrency, forKey: .fiatCurrency)
        try container.encode (fiatValue, forKey: .fiatValue)
        try container.encode (assetExchangeRate, forKey: .assetExchangeRate)
        try container.encode (baseRampFee, forKey: .baseRampFee)
        try container.encode (networkFee, forKey: .networkFee)
        try container.encode (appliedFee, forKey: .appliedFee)
        try container.encode (paymentMethodType, forKey: .paymentMethodType)
        try container.encode (finalTxHash, forKey: .finalTxHash)
        try container.encode (createdAt, forKey: .createdAt)
        try container.encode (updatedAt, forKey: .updatedAt)
        try container.encode (status, forKey: .status)
        try container.encode (escrowAddress, forKey: .escrowAddress)
        try container.encode (escrowDetailsHash, forKey: .escrowDetailsHash)
    }
}

extension OnrampPurchase.AssetInfo: Encodable {
    public enum CodeKeys: String, CodingKey {
        case address
        case symbol
        case type
        case name
        case decimals
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodeKeys.self)
        try container.encode (address, forKey: .address)
        try container.encode (symbol, forKey: .symbol)
        try container.encode (type, forKey: .type)
        try container.encode (name, forKey: .name)
        try container.encode (decimals, forKey: .decimals)
    }
}



extension OfframpSale: Encodable {
    
    public enum CodeKeys: String, CodingKey {
        case id
        case createdAt
        case crypto
        case fiat
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodeKeys.self)
        try container.encode (id, forKey: .id)
        try container.encode (createdAt, forKey: .createdAt)
        try container.encode (crypto, forKey: .crypto)
        try container.encode (fiat, forKey: .fiat)
    }
}

extension OfframpSale.Crypto: Encodable {
    public enum CodeKeys: String, CodingKey {
        case amount
        case assetInfo
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodeKeys.self)
        try container.encode (amount, forKey: .amount)
        try container.encode (assetInfo, forKey: .assetInfo)
    }
}

extension OfframpSale.Fiat: Encodable {
    public enum CodeKeys: String, CodingKey {
        case amount
        case assetInfo
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodeKeys.self)
        try container.encode (amount, forKey: .amount)
        try container.encode (currencySymbol, forKey: .currencySymbol)
    }
}


extension OfframpSale.Crypto: Encodable {
    public enum CodeKeys: String, CodingKey {
        case amount
        case assetInfo
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodeKeys.self)
        try container.encode (amount, forKey: .amount)
        try container.encode (assetInfo, forKey: .assetInfo)
    }
}

extension OfframpAssetInfo: Encodable {
    
    public enum CodeKeys: String, CodingKey {
        case address
        case chain
        case decimals
        case name
        case symbol
        case type
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodeKeys.self)
        try container.encode (address, forKey: .address)
        try container.encode (chain, forKey: .chain)
        try container.encode (decimals, forKey: .decimals)
        try container.encode (name, forKey: .name)
        try container.encode (symbol, forKey: .symbol)
        try container.encode (type, forKey: .type)
    }
}


