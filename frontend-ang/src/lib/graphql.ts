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

export const getSpecificCrate = graphql(`
    query GetCrateById($i: ID!) {
        getEuroCrateById(id: $i) {
            internalId
            name
            operationCenter
            deliveryState
            information
        }
    }
`);

export async function execute<TResult, TVariables>(
	query: TypedDocumentString<TResult, TVariables>,
	fetchFn = window.fetch,
	...[variables]: TVariables extends Record<string, never> ? [] : [TVariables]
) {
	const response = await fetchFn('http://localhost:8080/graphql', {
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