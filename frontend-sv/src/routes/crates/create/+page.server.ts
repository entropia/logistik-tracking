import type { Actions } from './$types';
import { message, superValidate } from "sveltekit-superforms";
import { zod4 } from "sveltekit-superforms/adapters";
import { createCrate as schema } from "$lib/schemas/crates";
import { graphqlWithAuthHandling } from "$lib/auth_util";
import { createCrate } from "$lib/graphql";
import { fail, redirect } from "@sveltejs/kit";

export const load = async (event) => {
	const form = await superValidate(zod4(schema));

	// Always return { form } in load functions
	return { form };
};

export const actions = {
	default: async (event) => {
		const form = await superValidate(event.request, zod4(schema));

		if (!form.valid) {
			// Return { form } and things will just work.
			return fail(400, { form });
		}

		let createdCrate = (await graphqlWithAuthHandling(event.url, createCrate, event.fetch, {
			name: form.data.name,
			deli: form.data.deliveryState,
			info: form.data.info || "",
			oc: form.data.operationCenter,
			jira: form.data.jiraTicket != null ? "LOC-" + form.data.jiraTicket : undefined
		})).data!!.createEuroCrate!!;

		if (!form.data.createMultiple) return redirect(303, "/crates/"+createdCrate.internalId);
		else return message(await superValidate({
			createMultiple: true
		}, zod4(schema)), `Kiste mit ID ${createdCrate.internalId} erstellt!`);
	}
} satisfies Actions;