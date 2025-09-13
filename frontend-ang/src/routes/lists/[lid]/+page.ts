import type { PageLoad } from './$types';
import {execute, getListById} from "$lib/graphql";
import {error} from "@sveltejs/kit";

export const load: PageLoad = async (event) => {
	let list = (await execute(getListById, event.fetch, {
		i: event.params.lid
	})).data!!.getPackingListById;
	if (!list) {
		error(404, "list not found");
	}
	return {
		list: list
	}
}