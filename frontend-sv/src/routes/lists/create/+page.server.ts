import type { Actions } from './$types';
import { error, redirect } from "@sveltejs/kit";
import { graphqlWithAuthHandling } from "$lib/auth_util";
import { fail, superValidate, message } from 'sveltekit-superforms';
import { createList } from '$lib/schemas/lists';
import { zod4 } from 'sveltekit-superforms/adapters';
import { createList as createListGql } from '$lib/graphql';

export const actions = {
	default: async (event) => {
		const form = await superValidate(event.request, zod4(createList));

		if (!form.valid) {
			// Return { form } and things will just work.
			return fail(400, { form });
		}

		let res = (await graphqlWithAuthHandling(event.url, createListGql, event.fetch, {
			name: form.data.name
		})).data!!.createPackingList!!

		return redirect(303, "/lists/"+res.packingListId);
	}
} satisfies Actions;