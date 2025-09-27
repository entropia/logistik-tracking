import type { PageLoad } from './$types';
import {execute, getListByIdAndAlsoGetAllCrates} from "$lib/graphql";
import {error} from "@sveltejs/kit";

export const load: PageLoad = async (event) => {
	let list = (await execute(getListByIdAndAlsoGetAllCrates, event.fetch, {
		i: event.params.lid
	})).data!!;
	if (!list.getPackingListById) {
		error(404, "list not found");
	}
	return {
		list: list.getPackingListById!!,
		crates: list.getEuroCrates
	}
}