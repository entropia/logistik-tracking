/* eslint-disable */
import * as types from './graphql';



/**
 * Map of all GraphQL operations in the project.
 *
 * This map has several performance disadvantages:
 * 1. It is not tree-shakeable, so it will include all operations in the project.
 * 2. It is not minifiable, so the string of a GraphQL query will be multiple times inside the bundle.
 * 3. It does not support dead code elimination, so it will add unused operations.
 *
 * Therefore it is highly recommended to use the babel or swc plugin for production.
 * Learn more about it here: https://the-guild.dev/graphql/codegen/plugins/presets/preset-client#reducing-bundle-size
 */
type Documents = {
    "\n    query GetAllCratesAllFields {\n        getEuroCrates {\n            internalId\n\t\t\tname\n\t\t\toperationCenter\n\t\t\tdeliveryState\n\t\t\tinformation\n        }\n    }\n": typeof types.GetAllCratesAllFieldsDocument,
    "\n\tquery GetAllLists {\n\t\tgetPackingLists {\n\t\t\tpackingListId\n\t\t\tname\n\t\t\tdeliveryStatet\n        }\n\t}\n": typeof types.GetAllListsDocument,
    "\n    query GetListByIdAndAlsoAllCrates($i: ID!) {\n        getPackingListById(id: $i) {\n            packingListId\n            name\n            deliveryStatet\n            packedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n        }\n\t\tgetEuroCrates {\n\t\t\tinternalId\n\t\t\toperationCenter\n\t\t\tname\n        }\n    }\n": typeof types.GetListByIdAndAlsoAllCratesDocument,
    "\n    query GetCrateById($i: ID!) {\n        getEuroCrateById(id: $i) {\n            internalId\n            name\n            operationCenter\n            deliveryState\n            information\n\t\t\tjiraId\n\t\t\tpackingList {\n                packingListId\n                name\n                deliveryStatet\n            }\n        }\n    }\n": typeof types.GetCrateByIdDocument,
    "\n    mutation UpdateCrate($which: ID!, $oc: OperationCenter!, $deli: DeliveryState!, $info: String!, $jira: String) {\n        modifyEuroCrate(id: $which, oc: $oc, deliveryState: $deli, info: $info, jiraIssue: $jira) {\n            internalId\n            operationCenter\n            deliveryState\n            information\n\t\t\tjiraId\n        }\n    }\n": typeof types.UpdateCrateDocument,
    "\n\tmutation UpdatePacking($id: ID!, $newstate: DeliveryState!) {\n\t\tsetPackingListDeliveryState(id: $id, deliveryState: $newstate) {\n\t\t\tpackingListId\n\t\t\tname\n\t\t\tdeliveryStatet\n            packedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n": typeof types.UpdatePackingDocument,
    "\n\tmutation CreateCrate($name: String!, $deli: DeliveryState!, $info: String!, $oc: OperationCenter!, $jira: String) {\n\t\tcreateEuroCrate(name: $name, deliveryState: $deli, info: $info, oc: $oc, jiraIssue: $jira) {\n\t\t\tinternalId\n\t\t}\n\t}\n": typeof types.CreateCrateDocument,
    "\n    mutation CreateList($name: String!) {\n        createPackingList(name: $name) {\n\t\t\tpackingListId\n        }\n    }\n": typeof types.CreateListDocument,
    "\n\tmutation RemoveCrates($pl: ID!, $crates: [ID!]!) {\n\t\tremoveCratesFromPackingList(id: $pl,crateIds: $crates) {\n\t\t\tpackedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n": typeof types.RemoveCratesDocument,
    "\n\tmutation AddCrates($pl: ID!, $crates: [ID!]!) {\n\t\taddCratesToPackingList(id: $pl,crateIds: $crates) {\n\t\t\tpackedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n": typeof types.AddCratesDocument,
    "\n    query GetMoreCrates($i: [ID!]!) {\n\t\tgetMultipleCratesById(id: $i) {\n            internalId\n\t\t\toperationCenter\n\t\t\tname\n\t\t\tdeliveryState\n\t\t}\n    }\n": typeof types.GetMoreCratesDocument,
};
const documents: Documents = {
    "\n    query GetAllCratesAllFields {\n        getEuroCrates {\n            internalId\n\t\t\tname\n\t\t\toperationCenter\n\t\t\tdeliveryState\n\t\t\tinformation\n        }\n    }\n": types.GetAllCratesAllFieldsDocument,
    "\n\tquery GetAllLists {\n\t\tgetPackingLists {\n\t\t\tpackingListId\n\t\t\tname\n\t\t\tdeliveryStatet\n        }\n\t}\n": types.GetAllListsDocument,
    "\n    query GetListByIdAndAlsoAllCrates($i: ID!) {\n        getPackingListById(id: $i) {\n            packingListId\n            name\n            deliveryStatet\n            packedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n        }\n\t\tgetEuroCrates {\n\t\t\tinternalId\n\t\t\toperationCenter\n\t\t\tname\n        }\n    }\n": types.GetListByIdAndAlsoAllCratesDocument,
    "\n    query GetCrateById($i: ID!) {\n        getEuroCrateById(id: $i) {\n            internalId\n            name\n            operationCenter\n            deliveryState\n            information\n\t\t\tjiraId\n\t\t\tpackingList {\n                packingListId\n                name\n                deliveryStatet\n            }\n        }\n    }\n": types.GetCrateByIdDocument,
    "\n    mutation UpdateCrate($which: ID!, $oc: OperationCenter!, $deli: DeliveryState!, $info: String!, $jira: String) {\n        modifyEuroCrate(id: $which, oc: $oc, deliveryState: $deli, info: $info, jiraIssue: $jira) {\n            internalId\n            operationCenter\n            deliveryState\n            information\n\t\t\tjiraId\n        }\n    }\n": types.UpdateCrateDocument,
    "\n\tmutation UpdatePacking($id: ID!, $newstate: DeliveryState!) {\n\t\tsetPackingListDeliveryState(id: $id, deliveryState: $newstate) {\n\t\t\tpackingListId\n\t\t\tname\n\t\t\tdeliveryStatet\n            packedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n": types.UpdatePackingDocument,
    "\n\tmutation CreateCrate($name: String!, $deli: DeliveryState!, $info: String!, $oc: OperationCenter!, $jira: String) {\n\t\tcreateEuroCrate(name: $name, deliveryState: $deli, info: $info, oc: $oc, jiraIssue: $jira) {\n\t\t\tinternalId\n\t\t}\n\t}\n": types.CreateCrateDocument,
    "\n    mutation CreateList($name: String!) {\n        createPackingList(name: $name) {\n\t\t\tpackingListId\n        }\n    }\n": types.CreateListDocument,
    "\n\tmutation RemoveCrates($pl: ID!, $crates: [ID!]!) {\n\t\tremoveCratesFromPackingList(id: $pl,crateIds: $crates) {\n\t\t\tpackedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n": types.RemoveCratesDocument,
    "\n\tmutation AddCrates($pl: ID!, $crates: [ID!]!) {\n\t\taddCratesToPackingList(id: $pl,crateIds: $crates) {\n\t\t\tpackedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n": types.AddCratesDocument,
    "\n    query GetMoreCrates($i: [ID!]!) {\n\t\tgetMultipleCratesById(id: $i) {\n            internalId\n\t\t\toperationCenter\n\t\t\tname\n\t\t\tdeliveryState\n\t\t}\n    }\n": types.GetMoreCratesDocument,
};

/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    query GetAllCratesAllFields {\n        getEuroCrates {\n            internalId\n\t\t\tname\n\t\t\toperationCenter\n\t\t\tdeliveryState\n\t\t\tinformation\n        }\n    }\n"): typeof import('./graphql').GetAllCratesAllFieldsDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n\tquery GetAllLists {\n\t\tgetPackingLists {\n\t\t\tpackingListId\n\t\t\tname\n\t\t\tdeliveryStatet\n        }\n\t}\n"): typeof import('./graphql').GetAllListsDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    query GetListByIdAndAlsoAllCrates($i: ID!) {\n        getPackingListById(id: $i) {\n            packingListId\n            name\n            deliveryStatet\n            packedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n        }\n\t\tgetEuroCrates {\n\t\t\tinternalId\n\t\t\toperationCenter\n\t\t\tname\n        }\n    }\n"): typeof import('./graphql').GetListByIdAndAlsoAllCratesDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    query GetCrateById($i: ID!) {\n        getEuroCrateById(id: $i) {\n            internalId\n            name\n            operationCenter\n            deliveryState\n            information\n\t\t\tjiraId\n\t\t\tpackingList {\n                packingListId\n                name\n                deliveryStatet\n            }\n        }\n    }\n"): typeof import('./graphql').GetCrateByIdDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    mutation UpdateCrate($which: ID!, $oc: OperationCenter!, $deli: DeliveryState!, $info: String!, $jira: String) {\n        modifyEuroCrate(id: $which, oc: $oc, deliveryState: $deli, info: $info, jiraIssue: $jira) {\n            internalId\n            operationCenter\n            deliveryState\n            information\n\t\t\tjiraId\n        }\n    }\n"): typeof import('./graphql').UpdateCrateDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n\tmutation UpdatePacking($id: ID!, $newstate: DeliveryState!) {\n\t\tsetPackingListDeliveryState(id: $id, deliveryState: $newstate) {\n\t\t\tpackingListId\n\t\t\tname\n\t\t\tdeliveryStatet\n            packedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n"): typeof import('./graphql').UpdatePackingDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n\tmutation CreateCrate($name: String!, $deli: DeliveryState!, $info: String!, $oc: OperationCenter!, $jira: String) {\n\t\tcreateEuroCrate(name: $name, deliveryState: $deli, info: $info, oc: $oc, jiraIssue: $jira) {\n\t\t\tinternalId\n\t\t}\n\t}\n"): typeof import('./graphql').CreateCrateDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    mutation CreateList($name: String!) {\n        createPackingList(name: $name) {\n\t\t\tpackingListId\n        }\n    }\n"): typeof import('./graphql').CreateListDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n\tmutation RemoveCrates($pl: ID!, $crates: [ID!]!) {\n\t\tremoveCratesFromPackingList(id: $pl,crateIds: $crates) {\n\t\t\tpackedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n"): typeof import('./graphql').RemoveCratesDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n\tmutation AddCrates($pl: ID!, $crates: [ID!]!) {\n\t\taddCratesToPackingList(id: $pl,crateIds: $crates) {\n\t\t\tpackedCrates {\n                internalId\n                name\n                operationCenter\n                deliveryState\n            }\n\t\t}\n\t}\n"): typeof import('./graphql').AddCratesDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    query GetMoreCrates($i: [ID!]!) {\n\t\tgetMultipleCratesById(id: $i) {\n            internalId\n\t\t\toperationCenter\n\t\t\tname\n\t\t\tdeliveryState\n\t\t}\n    }\n"): typeof import('./graphql').GetMoreCratesDocument;


export function graphql(source: string) {
  return (documents as any)[source] ?? {};
}
