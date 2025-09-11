import type { PageLoad } from './$types';
import {execute, getSpecificCrate} from "$lib/graphql";

export const load: PageLoad = async (event) => {
	return {
		crates: (await execute(getSpecificCrate, event.fetch, {
			i: event.params.cid
		})).data!!.getEuroCrateById!!
	}
}