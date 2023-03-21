import Ramp
import Foundation

@objc(RampSdk)
class RampSdk: RCTEventEmitter {

    @objc static override func requiresMainQueueSetup() -> Bool {
        return true
    }

    private var instanceId: String?
    private var sendCryptoResponseHandler: ((SendCryptoResultPayload) -> Void)?

    @objc(runRamp:)
    func runRamp(rawConfig: NSDictionary) -> Void {
        guard let config = rawConfig as? [String: Any] else {
            return
        }
        
        let configuration = mapConfig(rawConfig: config)
        instanceId = config["instanceId"] as? String
        
        let ramp = try! RampViewController(configuration: configuration)
        ramp.delegate = self

        let presentedViewController = RCTPresentedViewController()
        
        DispatchQueue.main.async {
            presentedViewController?.present(ramp, animated: true)
        }
    }
    
    @objc(onOfframpCryptoSent::)
    func onOfframpCryptoSent(txHash: String, error: String) -> Void {
        let payload = SendCryptoResultPayload(txHash: txHash)
        sendCryptoResponseHandler?(payload)
    }

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
        configuration.url = rawConfig["url"] as? String ?? "https://buy.ramp.network"
        configuration.webhookStatusUrl = rawConfig["webhookStatusUrl"] as? String ?? nil

        configuration.hostApiKey = rawConfig["hostApiKey"] as? String ?? nil
        configuration.deepLinkScheme = rawConfig["deepLinkScheme"] as? String ?? nil

        if let rawDefaultFlow = rawConfig["defaultFlow"] as? String {
            configuration.defaultFlow = Configuration.Flow(rawValue: rawDefaultFlow)
        }
        if let rawEnabledFlows = rawConfig["enabledFlows"] as? [String] {
            let enabledFlows = rawEnabledFlows.compactMap(Configuration.Flow.init(rawValue:))
            configuration.enabledFlows = Set(enabledFlows)
        }
        
        configuration.offrampWebhookV3Url = rawConfig["offrampWebhookV3Url"] as? String ?? nil
        configuration.useSendCryptoCallback = rawConfig["useSendCryptoCallback"] as? Bool ?? nil

        return configuration;
    }
}

extension RampSdk: RampDelegate {
    func ramp(_ rampViewController: RampViewController, didCreateOnrampPurchase purchase: OnrampPurchase, _ purchaseViewToken: String, _ apiUrl: URL) {
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

        self.sendCryptoResponseHandler = responseHandler
        
        sendEvent(withName: "offrampSendCrypto", body: [
            "instanceId": instanceId!,
            "payload": json
        ])
    }

    func ramp(_ rampViewController: RampViewController,
              didCreateOfframpSale sale: OfframpSale,
              _ saleViewToken: String,
              _ apiUrl: URL) {

        let data = try! JSONEncoder().encode(sale)
        let json = try! JSONSerialization.jsonObject(with: data)

        sendEvent(withName: "onOfframpSaleCreated", body: [
            "instanceId": instanceId!,
            "sale": json,
            "saleViewToken": saleViewToken,
            "apiUrl": apiUrl
        ])

    }
    
    func rampDidClose(_ rampViewController: RampViewController) {
        sendEvent(withName: "onRampDidClose", body: ["instanceId": instanceId!])
    }
}

extension SendCryptoPayload: Encodable {
    enum CodingKeys: CodingKey {
        case assetInfo
        case amount
        case address
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(assetInfo, forKey: .assetInfo)
        try container.encode(amount, forKey: .amount)
        try container.encode(address, forKey: .address)
    }
}
