import React, { useMemo, useState } from 'react';

import { StyleSheet, View, Button, Switch, Text, TextInput } from 'react-native';
import RampSdk, { WidgetEventTypes } from '@ramp-network/react-native-sdk';
import type { TAllEvents } from 'src/types';


export default function App() {
  const [url, setUrl] = useState('https://app.dev.ramp-network.org');
  const [hostApiKey, setHostApiKey] = useState('ku2jt8hctex6kwjsaz6ypphp26tbjysomwuj2yfj');
  const [enabledFlows, setEnabledFlows] = useState<Array<string>>(['ONRAMP', 'OFFRAMP']);
  const [defaultFlow, setDefaultFlow] = useState('ONRAMP');
  let callbackText = '';

  const [callbackTextLabel, setCallbackTextLabel] = useState('');

  let rampSdk: RampSdk = new RampSdk()
  .on(WidgetEventTypes.WIDGET_CLOSE, (event) => {
    widgetClose(event)
    callbackText += JSON.stringify(event) +"\n\n"
    console.log("CALLBACKTEXT: " +callbackText)
    setCallbackTextLabel(callbackText)
  }
  )
  .on(WidgetEventTypes.PURCHASE_CREATED, (event) => {
    console.log(`RampSdk.on(WidgetEventTypes.PURCHASE_CREATED)`, event);
    callbackText += JSON.stringify(event) +"\n\n"
    console.log("CALLBACKTEXT: " +callbackText)
    setCallbackTextLabel(callbackText)
  })
  .on(WidgetEventTypes.OFFRAMP_SALE_CREATED, (event) => {
    console.log(`RampSdk.on(WidgetEventTypes.OFFRAMP_SALE_CREATED)`, event);
    callbackText += JSON.stringify(event) +"\n\n"
    console.log("CALLBACKTEXT: " +callbackText)
    setCallbackTextLabel(callbackText)
  })
  .on(WidgetEventTypes.SEND_CRYPTO, (event) => {
    console.log(`RampSdk.on(WidgetEventTypes.SEND_CRYPTO)`, event);
    callbackText += JSON.stringify(event) +"\n\n"
    console.log("CALLBACKTEXT: " +callbackText)
    rampSdk.onOfframpCryptoSent("txHashTest","")
    setCallbackTextLabel(callbackText)
  });


  const widgetClose = (event: TAllEvents) => {
    console.log(`RampSdk.on(WidgetEventTypes.WIDGET_CLOSE)`, event);
  }

  const ramp =() => {

    rampSdk.unsubscribe('*', widgetClose)

    rampSdk.show({
        url: url,
        hostAppName: 'React Native Example',
        hostLogoUrl:
          'https://d33wubrfki0l68.cloudfront.net/554c3b0e09cf167f0281fda839a5433f2040b349/ecfc9/img/header_logo.svg',
        deepLinkScheme: 'ramprndemo',
        useSendCryptoCallback: true,
        hostApiKey: hostApiKey,
        enabledFlows: enabledFlows,
        defaultFlow: defaultFlow,
      })
  };

  return (
    <View style={styles.container}>
      <View style={styles.textContainer}>
        <Text>BASE URL:</Text>
      <TextInput
        value={url}
        onChangeText={setUrl}
      />
     </View>
     <View style={styles.textContainer}>
        <Text>Host API Key:</Text>
      <TextInput
        value={hostApiKey}
        onChangeText={setHostApiKey}
      />
      </View>
      <View style={styles.switchContainer}>
        <Text>Enabled Flows:</Text>
        <Switch
          value={enabledFlows.includes('ONRAMP')}
          onValueChange={value => {
            const flows = value ? [...enabledFlows, 'ONRAMP'] : enabledFlows.filter(flow => flow !== 'ONRAMP');
            setEnabledFlows(flows);
          }}
        />
        <Text>ONRAMP</Text>
         <Switch
          value={enabledFlows.includes('OFFRAMP')}
          onValueChange={value => {
            const flows = value ? [...enabledFlows, 'OFFRAMP'] : enabledFlows.filter(flow => flow !== 'OFFRAMP');
            setEnabledFlows(flows);
          }}
        />
        <Text>OFFRAMP</Text>
      </View>
      <View style={styles.switchContainer}>
        <Text>Default Flow:</Text>
      
        <Switch
          value={defaultFlow === 'ONRAMP'}
          onValueChange={value => setDefaultFlow(value ? 'ONRAMP' : 'OFFRAMP')}
        />
        <Text>ONRAMP</Text>
        <Switch
          value={defaultFlow === 'OFFRAMP'}
          onValueChange={value => setDefaultFlow(value ? 'OFFRAMP' : 'ONRAMP')}
        />
        <Text>OFFRAMP</Text>
      </View>
      <Button title="Run Ramp" onPress={ramp} />
      <View>
        <Text>CALLBACKS:</Text>
        <Text>{callbackTextLabel}</Text>
     </View>
    </View>
  );

        }

const styles = StyleSheet.create({
  container: {
    paddingTop: 45,
    paddingStart: 25,
    paddingEnd: 25,
    alignItems: 'stretch',
    justifyContent: 'center',
  },
  switchContainer: {
    flexDirection: 'row',
    paddingTop: 15,
    paddingBottom: 15,
    justifyContent: 'space-between'
  },
  textContainer: {
    paddingTop: 15,
    paddingBottom: 15
  },
});
