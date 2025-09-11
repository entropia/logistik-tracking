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
    "\n    query GetCrateById($i: ID!) {\n        getEuroCrateById(id: $i) {\n            internalId\n            name\n            operationCenter\n            deliveryState\n            information\n        }\n    }\n": typeof types.GetCrateByIdDocument,
};
const documents: Documents = {
    "\n    query GetAllCratesAllFields {\n        getEuroCrates {\n            internalId\n\t\t\tname\n\t\t\toperationCenter\n\t\t\tdeliveryState\n\t\t\tinformation\n        }\n    }\n": types.GetAllCratesAllFieldsDocument,
    "\n    query GetCrateById($i: ID!) {\n        getEuroCrateById(id: $i) {\n            internalId\n            name\n            operationCenter\n            deliveryState\n            information\n        }\n    }\n": types.GetCrateByIdDocument,
};

/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    query GetAllCratesAllFields {\n        getEuroCrates {\n            internalId\n\t\t\tname\n\t\t\toperationCenter\n\t\t\tdeliveryState\n\t\t\tinformation\n        }\n    }\n"): typeof import('./graphql').GetAllCratesAllFieldsDocument;
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    query GetCrateById($i: ID!) {\n        getEuroCrateById(id: $i) {\n            internalId\n            name\n            operationCenter\n            deliveryState\n            information\n        }\n    }\n"): typeof import('./graphql').GetCrateByIdDocument;


export function graphql(source: string) {
  return (documents as any)[source] ?? {};
}
