// ToDo use types from @ramp-network/ramp-instant-sdk

export interface IHostConfig {
  swapAsset?: string;
  swapAmount?: string;
  fiatValue?: string;
  fiatCurrency?: string;
  userAddress?: string;
  userEmailAddress?: string;
  hostApiKey?: string;
  hostLogoUrl: string;
  hostAppName: string;
  url?: string;
  variant?: string;
  webhookStatusUrl?: string;
  finalUrl?: string;
  containerNode?: string;
  selectedCountryCode?: string;
  defaultAsset?: string;
}

export interface IRampSdkConfig extends IHostConfig {
  instanceId: string;
}

export interface IPurchase {
  id: string;
  endTime: string | null; // purchase validity time, ISO date-time string
  asset: IAssetInfo; // description of the purchased asset (address, symbol, name, decimals)
  receiverAddress: string; // blockchain address of the buyer
  cryptoAmount: string; // number-string, in wei or token units
  fiatCurrency: string; // three-letter currency code
  fiatValue: number; // total value the user pays for the purchase, in fiatCurrency
  assetExchangeRate: number; // price of 1 whole token of purchased asset, in fiatCurrency
  baseRampFee: number; // base Ramp fee before any modifications, in fiatCurrency
  networkFee: number; // network fee for transferring the purchased asset, in fiatCurrency
  appliedFee: number; // final fee the user pays (included in fiatValue), in fiatCurrency
  paymentMethodType: TPaymentMethodType; // type of payment method used to pay for the swap - see values below
  finalTxHash?: string; // hash of the crypto transfer blockchain transaction, filled once available
  createdAt: string; // ISO date-time string
  updatedAt: string; // ISO date-time string
  status: TPurchaseStatus; // See available values below
  escrowAddress?: string; // filled only for escrow-backend purchases
  escrowDetailsHash?: string; // hash of purchase details used on-chain for escrow-based purchases
}

type TPaymentMethodType =
  | 'MANUAL_BANK_TRANSFER'
  | 'AUTO_BANK_TRANSFER'
  | 'CARD_PAYMENT'
  | 'APPLE_PAY';

type TPurchaseStatus =
  | 'INITIALIZED' // The purchase was initialized.
  | 'PAYMENT_STARTED' // An automated payment was initiated, eg. via card or open banking.
  | 'PAYMENT_IN_PROGRESS' // User completed the payment process.
  | 'PAYMENT_FAILED' // The last payment was cancelled, rejected, or otherwise failed.
  | 'PAYMENT_EXECUTED' // The last payment was successful.
  | 'FIAT_SENT' // Outgoing bank transfer was confirmed on the buyer's account.
  | 'FIAT_RECEIVED' // Payment was confirmed, final checks before crypto transfer.
  | 'RELEASING' // Crypto release started – transfer transaction or escrow release() tx was sent.
  | 'RELEASED' // Crypto asset was confirmed to be transferred to the buyer. A terminal state.
  | 'EXPIRED' // The time to pay for the purchase was exceeded. A terminal state.
  | 'CANCELLED'; // The purchase was cancelled and won't be continued. A terminal state.

interface IAssetInfo {
  address: string | null; // 0x-prefixed address for ERC-20 tokens, `null` for ETH
  symbol: string; // asset symbol, for example `ETH`, `DAI`, `USDC`
  type: string; // asset type & network, e.g. `ETH`, `ERC20`, `MATIC_ERC20`
  name: string;
  decimals: number; // token decimals, e.g. 18 for ETH/DAI, 6 for USDC
}

export enum WidgetEventTypes {
  PURCHASE_CREATED = 'PURCHASE_CREATED',
  WIDGET_CLOSE = 'WIDGET_CLOSE',
  ERROR = 'ERROR',
}

export interface IWidgetEvent {
  type: string;
  payload: any | null;
}

export interface IPurchaseCreatedEvent extends IWidgetEvent {
  type: WidgetEventTypes.PURCHASE_CREATED;
  payload: {
    purchase: IPurchase;
    purchaseViewToken: string;
    apiUrl: string;
  };
}

export interface IWidgetCloseEvent extends IWidgetEvent {
  type: WidgetEventTypes.WIDGET_CLOSE;
  payload: null;
}

export interface IWidgetErrorEvent extends IWidgetEvent {
  type: WidgetEventTypes.ERROR;
  payload: null;
}

// ToDo fix type
export type TEventListener = (event: any) => any;

export type TAllEvents =
  | IPurchaseCreatedEvent
  | IWidgetCloseEvent
  | IWidgetErrorEvent;

export type TEventListenerDict = {
  [EventType in TAllEvents['type']]: TEventListener[];
};
