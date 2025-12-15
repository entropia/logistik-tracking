import type { PageLoad } from './$types';
import {execute, getAllLists, NetworkResponseNotOkError} from "$lib/graphql";
import {handleAuthError} from "$lib/auth_util";
import {GetAllListsQuery} from "../../gen/graphql";


export const load: PageLoad = async (event) => {
	let bruh: GetAllListsQuery["getPackingLists"]
	try {
		 bruh = (await execute(getAllLists, event.fetch)).data!!.getPackingLists;
	} catch (e) {
		if (e instanceof NetworkResponseNotOkError) {
			handleAuthError(event.url, e.resp)
			throw "should not reach here";
		} else {
			throw e;
		}
	}
	return {
		lists: bruh
	}
}