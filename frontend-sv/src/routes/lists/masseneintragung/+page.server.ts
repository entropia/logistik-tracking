import type { Actions } from './$types';
import { getListByIdAndAlsoGetAllCrates } from "$lib/graphql";
import { error } from "@sveltejs/kit";
import { graphqlWithAuthHandling } from "$lib/auth_util";
import { fail, superValidate, message, setError } from 'sveltekit-superforms';
import { updateListDeliveryState, updateTargettedListState } from '$lib/schemas/lists';
import { zod4 } from 'sveltekit-superforms/adapters';
import { updateListPacking } from '$lib/graphql';
import { prepare_id } from '$lib/id_parser';

export const load = async (event) => {
    const form = await superValidate(zod4(updateTargettedListState));
    return {
        form
    };
};

export const actions = {
    default: async (event) => {
        console.log("updated")
        const form = await superValidate(event.request, zod4(updateTargettedListState));

        if (!form.valid) {
            // Return { form } and things will just work.
            return fail(400, { form });
        }

        let res = (await graphqlWithAuthHandling(event.url, updateListPacking, event.fetch, {
            id: prepare_id(form.data.id),
            newstate: form.data.deliveryState
        })).data!!.setPackingListDeliveryState;

        if (!res) {
            return setError(form, "id", "Kiste nicht gefunden");
        }

        return message(form, `Liste ${res.name} aktualisiert auf Status ${res.deliveryStatet}`);
    }
} satisfies Actions;