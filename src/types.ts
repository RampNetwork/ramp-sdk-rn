// ToDo use types from @ramp-network/ramp-instant-sdk

export interface IHostConfig {
  url?: string;
  hostAppName: string;
  hostLogoUrl: string;
  swapAsset?: string;
  swapAmount?: string;
  fiatCurrency?: string;
  fiatValue?: string;
  userAddress?: string;
  userEmailAddress?: string;
  selectedCountryCode?: string;
  defaultAsset?: string;
  webhookStatusUrl?: string;
  hostApiKey?: string;
  deepLinkScheme?: string;
  defaultFlow?: string;
  enabledFlow?: Array<string>;
  useSendCryptoCallback?: boolean;
  offrampWebhookV3Url?: string;
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


export interface IOfframpSale {
  id: String;
  createdAt: String; // ISO date-time string
  crypto: ICrypto;
  fiat: IFiat;
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

interface ICrypto {
   amount: string;
   assetInfo: IAssetInfo
}

interface IFiat {
  amount: number;
  currencySymbol: string;
}

export enum WidgetEventTypes {
  PURCHASE_CREATED = 'PURCHASE_CREATED',
  WIDGET_CLOSE = 'WIDGET_CLOSE',
  OFFRAMP_SALE_CREATED = 'OFFRAMP_SALE_CREATED',
  SEND_CRYPTO = 'SEND_CRYPTO'
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

export interface IOfframpSaleCreatedEvent extends IWidgetEvent {
  type: WidgetEventTypes.OFFRAMP_SALE_CREATED;
  payload: {
    sale: IOfframpSale,
    saleViewToken: string,
    apiUrl: string
  };
}

export interface ISendCryptoEvent extends IWidgetEvent {
  type: WidgetEventTypes.SEND_CRYPTO;
  payload: {
    assetInfo: IAssetInfo,
    amount: string,
    address: string
  };
}

// ToDo fix type
export type TEventListener = (event: any) => any;

export type TAllEvents = IPurchaseCreatedEvent | IWidgetCloseEvent | IOfframpSaleCreatedEvent | ISendCryptoEvent;

export type TEventListenerDict = {
  [EventType in TAllEvents['type']]: TEventListener[];
};
