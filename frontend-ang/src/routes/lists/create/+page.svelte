<script lang="ts">
	import {createList, execute} from "$lib/graphql";
	import {goto} from "$app/navigation";
	import * as Field from "$lib/components/ui/field";
	import {Input} from "$lib/components/ui/input";
	import {Button} from "$lib/components/ui/button";

	let form_state = $state({
        name: ""
    });
	async function handle_submit(event: SubmitEvent) {
		event.preventDefault()
		let resp = await execute(createList, fetch, {
			name: form_state.name
		})
		let updated = resp.data?.createPackingList
		if (updated) {
			await goto("/lists/"+updated.packingListId);
		}
	}
</script>
<!--todo: fix for shadcn-->
<h2 class="text-2xl mb-5 font-bold">Liste erstellen</h2>
<form onsubmit={handle_submit} class="w-full max-w-md">
    <Field.Set>
        <Field.Group>
            <Field.Field>
                <Field.Label for="name">Name</Field.Label>
                <Input id="name" type="text" placeholder="Name der Liste..." bind:value={form_state.name} required />
                <Field.Description>Beschreibender Name der Liste</Field.Description>
            </Field.Field>
        </Field.Group>
        <Field.Field>
            <Button type="submit">Speichern</Button>
        </Field.Field>
    </Field.Set>
</form>