// stores/user.js
import { writable } from 'svelte/store';
import {client} from "$lib/http_api";
import {components} from "../gen/openapi";



function createUserStore() {
	const { subscribe, set } = writable<components["schemas"]["UserDto"] | null>(null);

	return {
		subscribe,
		fetch: async () => {
			const response = await client.GET("/users/me");
			const user = response.data || null;
			set(user);
		}
	};
}

export const currentUser = createUserStore();