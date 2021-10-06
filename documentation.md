
---

id: react-native-sdk

title: ReactNative integration via SDK

---

  

****Ramp ReactNative SDK**** is a library that allows you to easily integrate ****Ramp**** into your ReactNative app and communicate with it.

  

## Overview

  

This guide describes how to add and use Ramp SDK to your React Native app




##  Install the Package

At the root level of your project, install the package with yarn or npm inside the terminal:

### yarn

    yarn add react-native-ramp-sdk

### npm 

    npm i react-native-ramp-sdk -s




## iOS integration

> Please ensure that you are using the latest XCode version, since the
> SDK is written in Swift 5.5. As of this writing, this is Xcode 13.0.
> 
> The minimum iOS version is 11.0. Make sure to include this in the
> Podfile: `platform :ios, '11.0'`

If your project doesn't have a Podfile, create it in the root directory of your project and paste the following code into it. Remember to change  `target`  name to the one you are using.
    
Add the following sources and the Ramp pod reference to proper  `target`  sections. 

    //Podfile
    platform :ios, '11.0'
    inhibit_all_warnings!
    
    source 'https://github.com/CocoaPods/Specs.git'
    source 'https://github.com/passbase/zoomauthentication-cocoapods-specs.git'
    source 'https://github.com/passbase/cocoapods-specs.git'
    source 'https://github.com/passbase/microblink-cocoapods-specs.git'
    
    target 'ReactNativeApp' do
	    use_frameworks!
	    pod 'Ramp', :git => 'git@github.com:RampNetwork/ramp-sdk-ios.git', :tag => 'v0.9.0'
    end

Then, navigate inside the terminal into your project folder and run the following command to install the Ramp SDK as a dependency:
`pod install`

In terminal you should see:

    $ pod install
    Analyzing dependencies
    Pre-downloading: `Ramp` from `git@github.com:RampNetwork/ramp-sdk-ios.git`, tag `v0.9.0`
    Downloading dependencies
    Installing Microblink (5.11.1)
    Installing Passbase (2.4.0)
    Installing Ramp (0.0.1)
    Installing ZoomAuthentication (8.7.1)
    Generating Pods project
    Integrating client project
    
    [!] Please close any current Xcode sessions and use `ExampleCocoapods.xcworkspace` for this project from now on.
    Pod installation complete! There is 1 dependency from the Podfile and 4 total pods installed.

### Project setup

After integrating Ramp SDK, you need to adjust your app's permissions.

Ramp uses the Passbase library for Know Your Customer process. In order for Passbase to work, you have to add app permissions for  _Camera Usage_  and  _Photo Library Usage_. 

Please add the following permissions to your app's `Info.plist`, so that the verification library can access a user's camera to run a verification. You can do this in the property list view or by code.

Right-click somewhere outside the table and select `Add Row`. Now add the entries like below.

![](https://gblobscdn.gitbook.com/assets%2F-LvuY9BUE6kxmJFKQiuP%2F-Lvurs2Af-6Dl5oY0aCc%2F-Lvut1EGXUM6ic8MGHqf%2Fios-permissions.png?alt=media&token=7dc039e2-1b9d-429d-a649-f09a060b198b)

Or if you prefer to do this step with code, right-click on `Info.plist` and select Open As -> Source Code. Add the lines below somewhere inside the `<dict> </dict>`

    <!-- permission strings to be include in info.plist -->
    <key>NSCameraUsageDescription</key>
    <string>Please give us access to your camera, to complete the verification.</string>
    <key>NSPhotoLibraryUsageDescription</key>
    <string>Please give us access to your photo library to verify you.</string>


## Android integration

> Note: Make sure your Android Studio also supports Kotlin. As of this writing, Android Studio Version 3.5.1 has Kotlin support. If you're using an older version, you can install Kotlin support under Android Studio → Preferences… → Plugins → Browse Repository → Type “Kotlin” in search box → Install


To install the Ramp Android SDK, add its repository in your root  `build.gradle`  at the end of repositories:


    allprojects {
	    repositories {
		    maven { url 'https://jitpack.io' }
		    maven { url 'https://button.passbase.com/__android' }
	    }
	 }

Please use `minSdkVersion`  `21` in your `build.gradle (Module:app)`

    android {   
        defaultConfig {
    	    minSdkVersion 21
    	    ..
    	    }
    	 }  

 

### ProGuard

If you are using ProGuard you might need to add the following options:

    -dontwarn okio.**
    -dontwarn retrofit2.Platform$Java8
    -keepattributes *Annotation*
    -keepclassmembers class * {
        @org.greenrobot.eventbus.Subscribe <methods>;
    }
    -keep enum org.greenrobot.eventbus.ThreadMode { *; }
    
    -keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
        <init>(java.lang.Throwable);
    }



## Initializing the SDK

  
## import 

First, import the module:

```js
     import  RampSdk  from  'react-native-ramp-sdk';
```

  




  


## Filling the configuration object with your data

  

Ramp widget allows you to provide some useful parameters before displaying it. Most of the parameters are optional. You can set options such as user wallet address, desired cryptocurrency and crypto amount, etc. In order to do so, we need to add those parameters to a `Config` object provided by SDK.

  

```

Config(

hostLogoUrl = "https://example.com/logo.png",

hostAppName = "My App",

userAddress = "user_blockchain_address",

// optional, skip this param to use the production URL

url = "https://ri-widget-staging.firebaseapp.com/",

swapAsset = "ETH",

fiatCurrency = "USD",

fiatValue = "10",

userEmailAddress = "test@example.com"

)

```
You can find the description of all available parameters in our [documentation](/configuration/).

  

## Implementing events

  

* `onPurchaseCreated(purchase: Purchase, purchaseViewToken: String, apiUrl: String)` is called when a purchase is created, and returns a `Purchase` object, containing all its parameters. All fields of are described in the documentation [here](/sdk-reference/#ramp-purchase-object).

* `onPurchaseFailed()` is called when Ramp fails in any aspect. You will see the reason of failure on the screen.

* `onWidgetClose()` is called when Ramp finishes the flow and can be closed, or user closed it manually.

  


  

## Starting the widget

  

That's it, now you just need to run the `show(...)` method to open the Ramp widget. This method takes `config` object that you created above.

  

```

rampSdk.show(..todo..)

```

  

## Example & Demo App

  

A full code example for a basic integration can be found below. You can find a demo integration showing how to integrate the Ramp SDK for Android [on our Github](https://github.com/RampNetwork/ramp-sdk-android/tree/master/demo).

  



## Troubleshooting

Build failed (xCode >= 12.0)

`Undefined symbol: __swift_FORCE_LOAD_$_swiftWebKit`

In Build Phases tab add **libswiftWebKit.tbd** to **Link Binary with Libraries**.

--->>obrazek<<----
