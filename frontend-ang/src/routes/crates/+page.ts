import type { PageLoad } from './$types';
import {execute, getAllCratesAllFields} from "$lib/graphql";


export const load: PageLoad = async (event) => {
	return {
		crates: (await execute(getAllCratesAllFields, event.fetch)).data!!
	}
}