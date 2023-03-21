#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(RampSdk, RCTEventEmitter)

RCT_EXTERN_METHOD(runRamp: (NSDictionary *)config)
RCT_EXTERN_METHOD(onOfframpCryptoSent: (NSString *)txHash error: (NSString *)error)

@end
