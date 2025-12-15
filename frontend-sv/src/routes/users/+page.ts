import type { PageLoad } from './$types';
import {handleAuthError} from "$lib/auth_util";
import {client} from "$lib/http_api";

export const load: PageLoad = async (event) => {
	let resp = await client.GET("/users", {
		fetch: event.fetch
	})
	if (!resp.response.ok || !resp.data) {
		handleAuthError(event.url, resp.response)
		throw "should not reach here";
	}
	return {
		users: resp.data
	}
}