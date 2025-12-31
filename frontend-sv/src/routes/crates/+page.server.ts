import {getAllCratesAllFields} from "$lib/graphql";
import {graphqlWithAuthHandling} from "$lib/auth_util";


export const load = async (event) => {
	return {
		crates: (await graphqlWithAuthHandling(event.url, getAllCratesAllFields, event.fetch)).data!!.getEuroCrates!!
	}
}