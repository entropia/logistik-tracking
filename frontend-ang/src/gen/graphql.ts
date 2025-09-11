/* eslint-disable */
import { DocumentTypeDecoration } from '@graphql-typed-document-node/core';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
};

export enum DeliveryState {
  AtGpn = 'AtGpn',
  AtHome = 'AtHome',
  Packing = 'Packing',
  Transport = 'Transport',
  WaitingForTransport = 'WaitingForTransport'
}

export type EuroCrate = {
  __typename: 'EuroCrate';
  deliveryState: DeliveryState;
  information: Maybe<Scalars['String']['output']>;
  internalId: Scalars['ID']['output'];
  name: Scalars['String']['output'];
  operationCenter: OperationCenter;
  packingList: Maybe<PackingList>;
};

export type Mutation = {
  __typename: 'Mutation';
  addCratesToPackingList: PackingList;
  createEuroCrate: EuroCrate;
  createPackingList: PackingList;
  deleteEuroCrate: Scalars['Boolean']['output'];
  deletePackingList: Scalars['Boolean']['output'];
  modifyEuroCrate: EuroCrate;
  removeCratesFromPackingList: PackingList;
  setPackingListDeliveryState: PackingList;
};


export type MutationAddCratesToPackingListArgs = {
  crateIds: Array<Scalars['ID']['input']>;
  id: Scalars['ID']['input'];
};


export type MutationCreateEuroCrateArgs = {
  deliveryState: DeliveryState;
  info?: InputMaybe<Scalars['String']['input']>;
  name: Scalars['String']['input'];
  oc: OperationCenter;
};


export type MutationCreatePackingListArgs = {
  name: Scalars['String']['input'];
};


export type MutationDeleteEuroCrateArgs = {
  id: Scalars['ID']['input'];
};


export type MutationDeletePackingListArgs = {
  id: Scalars['ID']['input'];
};


export type MutationModifyEuroCrateArgs = {
  deliveryState?: InputMaybe<DeliveryState>;
  id: Scalars['ID']['input'];
  info?: InputMaybe<Scalars['String']['input']>;
  oc?: InputMaybe<OperationCenter>;
};


export type MutationRemoveCratesFromPackingListArgs = {
  crateIds: Array<Scalars['ID']['input']>;
  id: Scalars['ID']['input'];
};


export type MutationSetPackingListDeliveryStateArgs = {
  deliveryState: DeliveryState;
  id: Scalars['ID']['input'];
};

export enum OperationCenter {
  Aussenbar = 'Aussenbar',
  Backoffice = 'Backoffice',
  Badges = 'Badges',
  Bar = 'Bar',
  BuildupAndTeardown = 'BuildupAndTeardown',
  Cocktailbar = 'Cocktailbar',
  Content = 'Content',
  Deko = 'Deko',
  DesignUndMotto = 'DesignUndMotto',
  Finanzen = 'Finanzen',
  Fruehstueck = 'Fruehstueck',
  Heralding = 'Heralding',
  Infodesk = 'Infodesk',
  Infrastruktur = 'Infrastruktur',
  Kaffeebar = 'Kaffeebar',
  Kueche = 'Kueche',
  Loc = 'LOC',
  LoungeControl = 'LoungeControl',
  LoungeTechnik = 'LoungeTechnik',
  Merchdesk = 'Merchdesk',
  Noc = 'NOC',
  Poc = 'POC',
  PresseUndSocialMedia = 'PresseUndSocialMedia',
  Projektleitung = 'Projektleitung',
  RaumDer1000Namen = 'RaumDer1000Namen',
  SafeR = 'SafeR',
  Schilder = 'Schilder',
  SilentHacking = 'SilentHacking',
  Spaeti = 'Spaeti',
  Trolle = 'Trolle',
  Voc = 'VOC',
  Woc = 'WOC'
}

export type PackingList = {
  __typename: 'PackingList';
  deliveryStatet: DeliveryState;
  name: Scalars['String']['output'];
  packedCrates: Array<EuroCrate>;
  packingListId: Scalars['ID']['output'];
};

export type Query = {
  __typename: 'Query';
  getEuroCrateById: Maybe<EuroCrate>;
  getEuroCrates: Array<EuroCrate>;
  getPackingListById: Maybe<PackingList>;
  getPackingLists: Array<PackingList>;
};


export type QueryGetEuroCrateByIdArgs = {
  id: Scalars['ID']['input'];
};


export type QueryGetPackingListByIdArgs = {
  id: Scalars['ID']['input'];
};

export type GetAllCratesAllFieldsQueryVariables = Exact<{ [key: string]: never; }>;


export type GetAllCratesAllFieldsQuery = { getEuroCrates: Array<{ __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState, information: string | null }> };

export type GetCrateByIdQueryVariables = Exact<{
  i: Scalars['ID']['input'];
}>;


export type GetCrateByIdQuery = { getEuroCrateById: { __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState, information: string | null } | null };

export class TypedDocumentString<TResult, TVariables>
  extends String
  implements DocumentTypeDecoration<TResult, TVariables>
{
  __apiType?: NonNullable<DocumentTypeDecoration<TResult, TVariables>['__apiType']>;
  private value: string;
  public __meta__?: Record<string, any> | undefined;

  constructor(value: string, __meta__?: Record<string, any> | undefined) {
    super(value);
    this.value = value;
    this.__meta__ = __meta__;
  }

  override toString(): string & DocumentTypeDecoration<TResult, TVariables> {
    return this.value;
  }
}

export const GetAllCratesAllFieldsDocument = new TypedDocumentString(`
    query GetAllCratesAllFields {
  getEuroCrates {
    internalId
    name
    operationCenter
    deliveryState
    information
  }
}
    `) as unknown as TypedDocumentString<GetAllCratesAllFieldsQuery, GetAllCratesAllFieldsQueryVariables>;
export const GetCrateByIdDocument = new TypedDocumentString(`
    query GetCrateById($i: ID!) {
  getEuroCrateById(id: $i) {
    internalId
    name
    operationCenter
    deliveryState
    information
  }
}
    `) as unknown as TypedDocumentString<GetCrateByIdQuery, GetCrateByIdQueryVariables>;