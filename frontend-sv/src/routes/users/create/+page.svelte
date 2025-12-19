<script lang="ts">
	import * as Field from "$lib/components/ui/field";
	import {Checkbox} from "$lib/components/ui/checkbox";
	import {Button} from "$lib/components/ui/button";
	import {client} from "$lib/http_api";
	import {Input} from "$lib/components/ui/input";
	import {toast} from "svelte-sonner";
	import {goto} from "$app/navigation";
    import { Save } from "@lucide/svelte";

	const allowedAuthorities = ["MANAGE_RESOURCES", "MANAGE_USERS", "PRINT"] as const;
	const mapping: Record<typeof allowedAuthorities[number], string> = {
		"MANAGE_RESOURCES": "Ressourcen verwalten",
		"MANAGE_USERS": "Nutzer verwalten",
		"PRINT": "Drucken"
	};

	type CompilerHack_FormState = {
		username: string,
		enabled: boolean,
		// hier spezifisch: damit es einen compiler fehler gibt wenn wir authorities ändern und die hier nicht vermerken
		authorities: (typeof allowedAuthorities[number])[],
		password: string
	}

	let form_state: CompilerHack_FormState = $state({
        username: "",
		enabled: true,
		authorities: [],
		password: ""
	})

	$inspect(form_state)

	async function handle_submit(ev: SubmitEvent) {
		ev.preventDefault()
		let x = await client.POST("/users", {
			body: form_state
		})
        await goto("/users/manage/"+x.data?.username)
		toast.success("Nutzer erstellt!")
	}

	function toggle_authority(state: boolean, e: (typeof allowedAuthorities[number])) {
		if (state) {
			// add
			if (!form_state.authorities.includes(e)) {
				form_state.authorities.push(e)
			}
		} else {
			// remove
			if (form_state.authorities.includes(e)) {
				form_state.authorities.splice(form_state.authorities.indexOf(e), 1)
			}
		}
	}
</script>

<svelte:head>
    <title>Nutzer erstellen</title>
</svelte:head>

<h2 class="text-2xl mb-5 font-bold">Nutzer erstellen</h2>

<form onsubmit={handle_submit} class="w-full max-w-md mb-5">
    <Field.Set>
        <Field.Field orientation="horizontal">
            <Checkbox bind:checked={form_state.enabled} id="enabled"></Checkbox>
            <Field.Label for="enabled">Aktiviert?</Field.Label>
        </Field.Field>
        <Field.Field>
            <Field.Label for="username">Nutzername ([a-zA-Z0-9_\-]+)</Field.Label>
            <Input id="username" required pattern="^[a-zA-Z0-9_\-]+$" placeholder="Nutzername" bind:value={form_state.username}></Input>
        </Field.Field>
        <Field.Field>
            <Field.Label for="newpw">Passwort</Field.Label>
            <Input type="password" id="newpw" bind:value={form_state.password} placeholder="Passwort" required></Input>
        </Field.Field>
        <Field.Set>
            <Field.Legend>Berechtigungen</Field.Legend>
            {#each allowedAuthorities as authority (authority)}
                <Field.Field orientation="horizontal">
                    <Checkbox id={"perm_"+authority} bind:checked={
                    () => form_state.authorities.includes(authority),
                    (c) => toggle_authority(c, authority)
                    }></Checkbox>
                    <Field.Label for={"perm_"+authority}>{mapping[authority]}</Field.Label>
                </Field.Field>
            {/each}
        </Field.Set>
        <Field.Field>
            <Button type="submit" class="grow">
				<Save />
				Speichern
			</Button>
        </Field.Field>
    </Field.Set>
</form>