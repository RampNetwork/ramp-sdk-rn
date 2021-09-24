import { TEventListenerDict, WidgetEventTypes } from './types';

export function initEventListenersDict(): TEventListenerDict {
  const widgetEventTypes = Array.from(Object.values(WidgetEventTypes));

  return [...widgetEventTypes].reduce<TEventListenerDict>(
    (listenersDict, eventType) => {
      listenersDict[eventType] = [];
      return listenersDict;
    },
    {} as TEventListenerDict
  );
}

export function getRandomIntString(): string {
  try {
    return String(crypto.getRandomValues(new Uint32Array(1))[0]);
  } catch (e) {
    // if `crypto` is not supported, fall back to Math.random
    // tslint:disable-next-line:no-magic-numbers
    return String(Math.floor(Math.random() * 10000000));
  }
}