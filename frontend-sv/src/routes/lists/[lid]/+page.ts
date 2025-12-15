import type { PageLoad } from './$types';
import {execute, getListByIdAndAlsoGetAllCrates, NetworkResponseNotOkError} from "$lib/graphql";
import {error} from "@sveltejs/kit";
import {handleAuthError} from "$lib/auth_util";
import {GetListByIdAndAlsoAllCratesQuery} from "../../../gen/graphql";

export const load: PageLoad = async (event) => {
	let list: GetListByIdAndAlsoAllCratesQuery;
	try {
		list = (await execute(getListByIdAndAlsoGetAllCrates, event.fetch, {
			i: event.params.lid
		})).data!!;
	} catch (e) {
		if (e instanceof NetworkResponseNotOkError) {
			handleAuthError(event.url, e.resp)
			throw "should not reach here";
		} else {
			throw e;
		}
	}
	if (!list.getPackingListById) {
		error(404, "list not found");
	}
	return {
		list: list.getPackingListById!!,
		crates: list.getEuroCrates
	}
}