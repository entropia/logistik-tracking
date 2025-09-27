import {graphql} from "../gen";
import {ExecutionResult} from "graphql/execution";
import {TypedDocumentString} from "../gen/graphql";

export const getAllCratesAllFields = graphql(`
    query GetAllCratesAllFields {
        getEuroCrates {
            internalId
			name
			operationCenter
			deliveryState
			information
        }
    }
`);

export const getAllLists = graphql(`
	query GetAllLists {
		getPackingLists {
			packingListId
			name
			deliveryStatet
        }
	}
`)

export const getListById = graphql(`
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
`)

export const getListByIdAndAlsoGetAllCrates = graphql(`
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
`);

export const getSpecificCrate = graphql(`
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
`);

export const updateCrate = graphql(`
    mutation UpdateCrate($which: ID!, $oc: OperationCenter!, $deli: DeliveryState!, $info: String!) {
        modifyEuroCrate(id: $which, oc: $oc, deliveryState: $deli, info: $info) {
            internalId
            operationCenter
            deliveryState
            information
        }
    }
`);

export const updateListPacking = graphql(`
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
`)

export const createCrate = graphql(`
	mutation CreateCrate($name: String!, $deli: DeliveryState!, $info: String!, $oc: OperationCenter!) {
		createEuroCrate(name: $name, deliveryState: $deli, info: $info, oc: $oc) {
			internalId
		}
	}
`)

export const createList = graphql(`
    mutation CreateList($name: String!) {
        createPackingList(name: $name) {
			packingListId
        }
    }
`)

export const removeCratesFromList = graphql(`
	mutation RemoveCrates($pl: ID!, $crates: [ID!]!) {
		removeCratesFromPackingList(id: $pl,crateIds: $crates) {
			packedCrates {
                internalId
                name
                operationCenter
                deliveryState
            }
		}
	}
`)
export const addCratesToList = graphql(`
	mutation AddCrates($pl: ID!, $crates: [ID!]!) {
		addCratesToPackingList(id: $pl,crateIds: $crates) {
			packedCrates {
                internalId
                name
                operationCenter
                deliveryState
            }
		}
	}
`)

import { PUBLIC_API_URL } from '$env/static/public';

export async function execute<TResult, TVariables>(
	query: TypedDocumentString<TResult, TVariables>,
	fetchFn = window.fetch,
	...[variables]: TVariables extends Record<string, never> ? [] : [TVariables]
) {
	const response = await fetchFn(PUBLIC_API_URL+'/graphql', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			Accept: 'application/graphql-response+json'
		},
		body: JSON.stringify({
			query,
			variables
		})
	})

	if (!response.ok) {
		throw new Error('Network response was not ok')
	}

	return (await response.json()) as ExecutionResult<TResult>
}