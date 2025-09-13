import type { PageLoad } from './$types';
import {execute, getAllLists} from "$lib/graphql";


export const load: PageLoad = async (event) => {
	return {
		lists: (await execute(getAllLists, event.fetch)).data!!
	}
}