import type { PageLoad } from './$types';
import {execute, getSpecificCrate, NetworkResponseNotOkError} from "$lib/graphql";
import {error} from "@sveltejs/kit";
import {handleAuthError} from "$lib/auth_util";
import {GetCrateByIdQuery} from "../../../gen/graphql";

export const load: PageLoad = async (event) => {
	let res: GetCrateByIdQuery["getEuroCrateById"];
	try {
		res = (await execute(getSpecificCrate, event.fetch, {
			i: event.params.cid
		})).data!!.getEuroCrateById;
	} catch (e) {
		if (e instanceof NetworkResponseNotOkError) {
			handleAuthError(event.url, e.resp)
			throw "should not reach here";
		} else {
			throw e;
		}
	}
	if (!res) {
		error(404, "crate not found");
	}
	return {
		crates: res!!
	}
}