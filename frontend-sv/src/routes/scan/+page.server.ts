import type { Actions } from './$types';
import { error, redirect } from "@sveltejs/kit";
import { fail, superValidate } from 'sveltekit-superforms';
import { zod4 } from 'sveltekit-superforms/adapters';
import { findResource } from '$lib/schemas/scan';
import { stripIndicatorAndZeros } from '$lib/id_parser';

export const load = async (event) => {
    const form = await superValidate(zod4(findResource));
    return {
        form
    };
};

export const actions = {
    default: async (event) => {
        const form = await superValidate(event.request, zod4(findResource));

        if (!form.valid) {
            // Return { form } and things will just work.
            return fail(400, { form });
        }

        let id = form.data.id;
        const indicator = id[0];
        
        if (indicator == 'C' || indicator == 'c') {
            // crate, redirect
            return redirect(303, "/crates/"+stripIndicatorAndZeros(id))
        } else if (indicator == 'L' || indicator == 'l') {
            // list, redirect
            return redirect(303, "/lists/"+stripIndicatorAndZeros(id))
        } else {
            // some unknown resource..?
            return error(400, "unknown resource?");
        }
    }
} satisfies Actions;