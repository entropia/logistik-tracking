import type { PageLoad } from './$types';
import {execute, getAllCratesAllFields, NetworkResponseNotOkError} from "$lib/graphql";
import {handleAuthError} from "$lib/auth_util";
import {GetAllCratesAllFieldsQuery} from "../../gen/graphql";


export const load: PageLoad = async (event) => {
	let res: GetAllCratesAllFieldsQuery;
	try {
		res = (await execute(getAllCratesAllFields, event.fetch)).data!!;
	} catch (e) {
		if (e instanceof NetworkResponseNotOkError) {
			handleAuthError(event.url, e.resp)
			throw "should not reach here";
		} else {
			throw e;
		}
	}

	return {
		crates: res
	}
}