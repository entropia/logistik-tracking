import type { Actions } from './$types';
import { deleteList, getListByIdAndAlsoGetAllCrates } from "$lib/graphql";
import { error, redirect } from "@sveltejs/kit";
import { graphqlWithAuthHandling } from "$lib/auth_util";
import { fail, superValidate, message } from 'sveltekit-superforms';
import { updateListDeliveryState } from '$lib/schemas/lists';
import { zod4 } from 'sveltekit-superforms/adapters';
import { updateListPacking } from '$lib/graphql';

export const load = async (event) => {
	let list = (await graphqlWithAuthHandling(event.url, getListByIdAndAlsoGetAllCrates, event.fetch, {
		i: event.params.lid
	})).data!!;
	if (!list.getPackingListById) {
		error(404, "list not found");
	}
	const form = await superValidate({
		deliveryState: list.getPackingListById.deliveryStatet
	}, zod4(updateListDeliveryState));
	return {
		form,
		list: list.getPackingListById!!,
		crates: list.getEuroCrates
	};
};

export const actions = {
	update: async (event) => {
		const form = await superValidate(event.request, zod4(updateListDeliveryState));

		if (!form.valid) {
			// Return { form } and things will just work.
			return fail(400, { form });
		}

		let res = (await graphqlWithAuthHandling(event.url, updateListPacking, event.fetch, {
			id: event.params.lid,
			newstate: form.data.deliveryState
		})).data!!.setPackingListDeliveryState!!;

		return {
			form,
			res
		};
	},
	delete: async (event) => {
		// ok
		(await graphqlWithAuthHandling(event.url, deleteList, event.fetch, {
			i: event.params.lid
		}));

		return redirect(303, "/lists");
	}
} satisfies Actions;