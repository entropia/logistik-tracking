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
  information: Scalars['String']['output'];
  internalId: Scalars['ID']['output'];
  name: Scalars['String']['output'];
  operationCenter: OperationCenter;
  packingList: Maybe<PackingList>;
};

export type Mutation = {
  __typename: 'Mutation';
  addCratesToPackingList: Maybe<PackingList>;
  createEuroCrate: EuroCrate;
  createPackingList: PackingList;
  deleteEuroCrate: Scalars['Boolean']['output'];
  deletePackingList: Scalars['Boolean']['output'];
  modifyEuroCrate: Maybe<EuroCrate>;
  removeCratesFromPackingList: Maybe<PackingList>;
  setPackingListDeliveryState: Maybe<PackingList>;
};


export type MutationAddCratesToPackingListArgs = {
  crateIds: Array<Scalars['ID']['input']>;
  id: Scalars['ID']['input'];
};


export type MutationCreateEuroCrateArgs = {
  deliveryState: DeliveryState;
  info: Scalars['String']['input'];
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
  info: Scalars['String']['input'];
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


export type GetAllCratesAllFieldsQuery = { getEuroCrates: Array<{ __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState, information: string }> };

export type GetAllListsQueryVariables = Exact<{ [key: string]: never; }>;


export type GetAllListsQuery = { getPackingLists: Array<{ __typename: 'PackingList', packingListId: string, name: string, deliveryStatet: DeliveryState }> };

export type GetListByIdQueryVariables = Exact<{
  i: Scalars['ID']['input'];
}>;


export type GetListByIdQuery = { getPackingListById: { __typename: 'PackingList', packingListId: string, name: string, deliveryStatet: DeliveryState, packedCrates: Array<{ __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState }> } | null };

export type GetListByIdAndAlsoAllCratesQueryVariables = Exact<{
  i: Scalars['ID']['input'];
}>;


export type GetListByIdAndAlsoAllCratesQuery = { getPackingListById: { __typename: 'PackingList', packingListId: string, name: string, deliveryStatet: DeliveryState, packedCrates: Array<{ __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState }> } | null, getEuroCrates: Array<{ __typename: 'EuroCrate', internalId: string, operationCenter: OperationCenter, name: string }> };

export type GetCrateByIdQueryVariables = Exact<{
  i: Scalars['ID']['input'];
}>;


export type GetCrateByIdQuery = { getEuroCrateById: { __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState, information: string, packingList: { __typename: 'PackingList', packingListId: string, name: string, deliveryStatet: DeliveryState } | null } | null };

export type UpdateCrateMutationVariables = Exact<{
  which: Scalars['ID']['input'];
  oc: OperationCenter;
  deli: DeliveryState;
  info: Scalars['String']['input'];
}>;


export type UpdateCrateMutation = { modifyEuroCrate: { __typename: 'EuroCrate', internalId: string, operationCenter: OperationCenter, deliveryState: DeliveryState, information: string } | null };

export type UpdatePackingMutationVariables = Exact<{
  id: Scalars['ID']['input'];
  newstate: DeliveryState;
}>;


export type UpdatePackingMutation = { setPackingListDeliveryState: { __typename: 'PackingList', packingListId: string, name: string, deliveryStatet: DeliveryState, packedCrates: Array<{ __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState }> } | null };

export type CreateCrateMutationVariables = Exact<{
  name: Scalars['String']['input'];
  deli: DeliveryState;
  info: Scalars['String']['input'];
  oc: OperationCenter;
}>;


export type CreateCrateMutation = { createEuroCrate: { __typename: 'EuroCrate', internalId: string } };

export type CreateListMutationVariables = Exact<{
  name: Scalars['String']['input'];
}>;


export type CreateListMutation = { createPackingList: { __typename: 'PackingList', packingListId: string } };

export type RemoveCratesMutationVariables = Exact<{
  pl: Scalars['ID']['input'];
  crates: Array<Scalars['ID']['input']> | Scalars['ID']['input'];
}>;


export type RemoveCratesMutation = { removeCratesFromPackingList: { __typename: 'PackingList', packedCrates: Array<{ __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState }> } | null };

export type AddCratesMutationVariables = Exact<{
  pl: Scalars['ID']['input'];
  crates: Array<Scalars['ID']['input']> | Scalars['ID']['input'];
}>;


export type AddCratesMutation = { addCratesToPackingList: { __typename: 'PackingList', packedCrates: Array<{ __typename: 'EuroCrate', internalId: string, name: string, operationCenter: OperationCenter, deliveryState: DeliveryState }> } | null };

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
export const GetAllListsDocument = new TypedDocumentString(`
    query GetAllLists {
  getPackingLists {
    packingListId
    name
    deliveryStatet
  }
}
    `) as unknown as TypedDocumentString<GetAllListsQuery, GetAllListsQueryVariables>;
export const GetListByIdDocument = new TypedDocumentString(`
    query GetListById($i: ID!) {
  getPackingListById(id: $i) {
    packingListId
    name
    deliveryStatet
    packedCrates {
      internalId
      name
      operationCenter
      deliveryState
    }
  }
}
    `) as unknown as TypedDocumentString<GetListByIdQuery, GetListByIdQueryVariables>;
export const GetListByIdAndAlsoAllCratesDocument = new TypedDocumentString(`
    query GetListByIdAndAlsoAllCrates($i: ID!) {
  getPackingListById(id: $i) {
    packingListId
    name
    deliveryStatet
    packedCrates {
      internalId
      name
      operationCenter
      deliveryState
    }
  }
  getEuroCrates {
    internalId
    operationCenter
    name
  }
}
    `) as unknown as TypedDocumentString<GetListByIdAndAlsoAllCratesQuery, GetListByIdAndAlsoAllCratesQueryVariables>;
export const GetCrateByIdDocument = new TypedDocumentString(`
    query GetCrateById($i: ID!) {
  getEuroCrateById(id: $i) {
    internalId
    name
    operationCenter
    deliveryState
    information
    packingList {
      packingListId
      name
      deliveryStatet
    }
  }
}
    `) as unknown as TypedDocumentString<GetCrateByIdQuery, GetCrateByIdQueryVariables>;
export const UpdateCrateDocument = new TypedDocumentString(`
    mutation UpdateCrate($which: ID!, $oc: OperationCenter!, $deli: DeliveryState!, $info: String!) {
  modifyEuroCrate(id: $which, oc: $oc, deliveryState: $deli, info: $info) {
    internalId
    operationCenter
    deliveryState
    information
  }
}
    `) as unknown as TypedDocumentString<UpdateCrateMutation, UpdateCrateMutationVariables>;
export const UpdatePackingDocument = new TypedDocumentString(`
    mutation UpdatePacking($id: ID!, $newstate: DeliveryState!) {
  setPackingListDeliveryState(id: $id, deliveryState: $newstate) {
    packingListId
    name
    deliveryStatet
    packedCrates {
      internalId
      name
      operationCenter
      deliveryState
    }
  }
}
    `) as unknown as TypedDocumentString<UpdatePackingMutation, UpdatePackingMutationVariables>;
export const CreateCrateDocument = new TypedDocumentString(`
    mutation CreateCrate($name: String!, $deli: DeliveryState!, $info: String!, $oc: OperationCenter!) {
  createEuroCrate(name: $name, deliveryState: $deli, info: $info, oc: $oc) {
    internalId
  }
}
    `) as unknown as TypedDocumentString<CreateCrateMutation, CreateCrateMutationVariables>;
export const CreateListDocument = new TypedDocumentString(`
    mutation CreateList($name: String!) {
  createPackingList(name: $name) {
    packingListId
  }
}
    `) as unknown as TypedDocumentString<CreateListMutation, CreateListMutationVariables>;
export const RemoveCratesDocument = new TypedDocumentString(`
    mutation RemoveCrates($pl: ID!, $crates: [ID!]!) {
  removeCratesFromPackingList(id: $pl, crateIds: $crates) {
    packedCrates {
      internalId
      name
      operationCenter
      deliveryState
    }
  }
}
    `) as unknown as TypedDocumentString<RemoveCratesMutation, RemoveCratesMutationVariables>;
export const AddCratesDocument = new TypedDocumentString(`
    mutation AddCrates($pl: ID!, $crates: [ID!]!) {
  addCratesToPackingList(id: $pl, crateIds: $crates) {
    packedCrates {
      internalId
      name
      operationCenter
      deliveryState
    }
  }
}
    `) as unknown as TypedDocumentString<AddCratesMutation, AddCratesMutationVariables>;