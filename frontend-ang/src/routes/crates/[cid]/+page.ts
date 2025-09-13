import type { PageLoad } from './$types';
import {execute, getSpecificCrate} from "$lib/graphql";
import {error} from "@sveltejs/kit";

export const load: PageLoad = async (event) => {
	let res = (await execute(getSpecificCrate, event.fetch, {
		i: event.params.cid
	})).data!!.getEuroCrateById;
	if (!res) {
		error(404, "crate not found");
	}
	return {
		crates: res!!
	}
}