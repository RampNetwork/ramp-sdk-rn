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
  return String(Math.floor(Math.random() * 10000000));
}
