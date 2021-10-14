import { NativeModules, NativeEventEmitter } from 'react-native';

import {
  IHostConfig,
  IRampSdkConfig,
  TAllEvents,
  TEventListenerDict,
  WidgetEventTypes,
} from './types';
import { getRandomIntString, initEventListenersDict } from './utils';

interface TRampSdkNativeModule {
  runRamp(config: IRampSdkConfig): void;
}

const RampSdkNativeModule: TRampSdkNativeModule = NativeModules.RampSdk;

const RampEvents = new NativeEventEmitter(RampSdkNativeModule as any);

export default class RampSdk {
  private _listeners: TEventListenerDict = initEventListenersDict();
  private _instanceId: string;

  constructor(private config: IHostConfig) {
    this._instanceId = getRandomIntString();
    this._subscribeToRampEvents();
  }

  public show(): RampSdk {
    RampSdkNativeModule.runRamp({
      instanceId: this._instanceId,
      ...this.config,
    });

    return this;
  }

  public on<T extends TAllEvents>(
    type: T['type'] | '*',
    callback: (event: T) => any
  ): RampSdk {
    if (type !== '*' && !this._listeners[type]) {
      // tslint:disable-next-line:no-console
      console.warn(
        `Unknown / unsupported event name - '${type}'. This listener will have no effect.`
      );
    }

    if (type === '*') {
      const allTypes = Object.values(this._listeners);
      allTypes.forEach((eventHandlers) => eventHandlers.push(callback));
    } else {
      this._listeners[type].push(callback);
    }

    return this;
  }

  public unsubscribe(
    type: TAllEvents['type'] | '*',
    callback: (event: TAllEvents) => any
  ): RampSdk {
    if (type === '*') {
      const allTypes = Object.entries(this._listeners);

      allTypes.forEach(([key, eventHandlers]) => {
        const filteredHandlers = eventHandlers.filter((c) => c !== callback);
        this._listeners[key as TAllEvents['type']] = filteredHandlers;
      });
    } else {
      this._listeners[type] = this._listeners[type].filter(
        (c) => c !== callback
      );
    }

    return this;
  }

  private _subscribeToRampEvents() {
    RampEvents.addListener('onRamp', (event) => {
      console.log('onRamp', event);
      if (event.instanceId !== this._instanceId) {
        return;
      }
      this._dispatchEvent({
        type: WidgetEventTypes.PURCHASE_CREATED,
        payload: {
          purchase: event.purchase,
          purchaseViewToken: event.purchaseViewToken,
          apiUrl: event.apiUrl,
        },
      });
    });

    RampEvents.addListener('onRampPurchaseDidFail', (event) => {
      console.log('onRampPurchaseDidFail', event);
      if (event.instanceId !== this._instanceId) {
        return;
      }
      this._dispatchEvent({
        type: WidgetEventTypes.ERROR,
        payload: null,
      });
    });

    RampEvents.addListener('onRampDidClose', (event) => {
      console.log('onRampDidClose', event);
      if (event.instanceId !== this._instanceId) {
        return;
      }

      this._dispatchEvent({
        type: WidgetEventTypes.WIDGET_CLOSE,
        payload: null,
      });
    });
  }

  private _dispatchEvent(event: TAllEvents): void {
    const { type } = event;

    this._listeners[type].forEach((callback) => callback(event));
  }
}
