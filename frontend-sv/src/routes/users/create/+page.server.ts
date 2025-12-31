import type { Actions } from './$types';
import { redirect } from "@sveltejs/kit";
import { fail, superValidate } from 'sveltekit-superforms';
import { zod4 } from 'sveltekit-superforms/adapters';
import { createUser } from '$lib/schemas/users';
import { client } from "$lib/http_api";

export const load = async (event) => {
    const form = await superValidate(zod4(createUser));
    return {
        form
    };
};

export const actions = {
    default: async (event) => {
        const form = await superValidate(event.request, zod4(createUser));

        if (!form.valid) {
            // Return { form } and things will just work.
            return fail(400, { form });
        }

        console.log(form.data)

        let x = await client.POST("/users", {
			body: {
                authorities: form.data.authorities,
                enabled: form.data.active,
                password: form.data.password,
                username: form.data.name
            },
            fetch: event.fetch
		});
        console.log(x)

        return redirect(303, "/users/manage/"+x.data!!.username);
    }
} satisfies Actions;