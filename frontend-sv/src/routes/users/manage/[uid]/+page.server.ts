import type { Actions } from './$types';
import { fail, message, superValidate } from 'sveltekit-superforms';
import { zod4 } from 'sveltekit-superforms/adapters';
import { modifyUser } from '$lib/schemas/users';
import { client } from "$lib/http_api";
import { handleAuthError } from '$lib/auth_util';

export const load = async (event) => {
    let resp = await client.GET("/users/other/{name}", {
        fetch: event.fetch,
        params: {
            path: {
                name: event.params.uid
            }
        }
    })
    if (!resp.response.ok || !resp.data) {
        handleAuthError(event.url, resp.response)
        throw "should not reach here";
    }
    return {
        form: await superValidate({
            active: resp.data.enabled,
            authorities: resp.data.authorities
        }, zod4(modifyUser)),
        user: resp.data
    }
}

export const actions = {
    default: async (event) => {
        const form = await superValidate(event.request, zod4(modifyUser));

        if (!form.valid) {
            // Return { form } and things will just work.
            return fail(400, { form });
        }

        await client.PUT("/users", {
			body: {
				username: event.params.uid,
                authorities: form.data.authorities,
                active: form.data.active,
                password: form.data.password != undefined && form.data.password != "" ? form.data.password : undefined
            },
            fetch: event.fetch
        })

        return message(form, "Nutzer aktualisiert!");
    }
} satisfies Actions;