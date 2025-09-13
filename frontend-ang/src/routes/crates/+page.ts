import type { PageLoad } from './$types';
import {execute, getAllCratesAllFields} from "$lib/graphql";


export const load: PageLoad = async (event) => {
	let res = (await execute(getAllCratesAllFields, event.fetch)).data;

	return {
		crates: res!!
	}
}