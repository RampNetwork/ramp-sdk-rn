import React, { useMemo, useState } from 'react';

import { StyleSheet, View, Button, Switch, Text } from 'react-native';
import RampSdk from 'react-native-ramp-sdk';

type ENV = 'DEV' | 'STAGING' | 'PROD';

const envToUrl: { [env in ENV]: string } = {
  DEV: 'https://ri-widget-dev.firebaseapp.com',
  STAGING: 'https://ri-widget-staging.firebaseapp.com',
  PROD: 'https://ri-widget-prod.firebaseapp.com',
};

export default function App() {
  const [env, setEnv] = useState<ENV | undefined>('DEV');
  const ramp = useMemo(() => {
    if (!env) {
      return undefined;
    }

    return new RampSdk({
      url: envToUrl[env],
      hostAppName: 'React Native Example',
      hostLogoUrl:
        'https://d33wubrfki0l68.cloudfront.net/554c3b0e09cf167f0281fda839a5433f2040b349/ecfc9/img/header_logo.svg',
    }).on('*', (event) => {
      console.log(`RampSdk.on('*')`, event);
    });
  }, [env]);

  return (
    <View style={styles.container}>
      <Text>Select ENV</Text>

      <View style={styles.switchContainer}>
        <Text>DEV</Text>
        <Switch
          value={env === 'DEV'}
          onValueChange={(v) => (v ? setEnv('DEV') : setEnv(undefined))}
        />
      </View>

      <View style={styles.switchContainer}>
        <Text>STAGING</Text>
        <Switch
          value={env === 'STAGING'}
          onValueChange={(v) => (v ? setEnv('STAGING') : setEnv(undefined))}
        />
      </View>

      <View style={styles.switchContainer}>
        <Text>PROD</Text>
        <Switch
          value={env === 'PROD'}
          onValueChange={(v) => (v ? setEnv('PROD') : setEnv(undefined))}
        />
      </View>

      <Button
        disabled={env === undefined}
        title={`Run Ramp ${env ?? ''}`}
        onPress={() => ramp?.show()}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  switchContainer: {
    flexDirection: 'row',
    width: 150,
    justifyContent: 'space-between',
  },
});
