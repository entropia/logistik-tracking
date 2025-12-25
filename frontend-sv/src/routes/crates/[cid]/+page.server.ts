import type { Actions } from './$types';
import { message, superValidate } from "sveltekit-superforms";
import { zod4 } from "sveltekit-superforms/adapters";
import { updateCrate as schema } from "$lib/schemas/crates";
import { graphqlWithAuthHandling } from "$lib/auth_util";
import { execute, getSpecificCrate, NetworkResponseNotOkError, updateCrate } from "$lib/graphql";
import { error, fail } from "@sveltejs/kit";

export const load = async (event) => {
	let res = (await graphqlWithAuthHandling(event.url, getSpecificCrate, event.fetch, {
		i: event.params.cid
	})).data!!.getEuroCrateById;
	if (!res) {
		error(404, "crate not found");
		// throw "should not reach here";
	}

	const form = await superValidate({
		operationCenter: res.operationCenter,
		deliveryState: res.deliveryState,
		info: res.information,
		// unschön aber muss
		jiraTicket: res.jiraId ? parseInt(res.jiraId.substring(4)) : null
	}, zod4(schema));

	// Always return { form } in load functions
	return { form, crate: res };
};

export const actions = {
	default: async (event) => {
		const form = await superValidate(event.request, zod4(schema));

		if (!form.valid) {
			// Return { form } and things will just work.
			return fail(400, { form });
		}

		(await graphqlWithAuthHandling(event.url, updateCrate, event.fetch, {
			which: event.params.cid,
			deli: form.data.deliveryState,
			info: form.data.info || "",
			oc: form.data.operationCenter,
			jira: form.data.jiraTicket != null ? "LOC-" + form.data.jiraTicket : undefined
		}));

		return message(form, "Gespeichert!");
	}
} satisfies Actions;