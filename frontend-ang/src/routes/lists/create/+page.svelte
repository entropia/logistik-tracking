<script lang="ts">
	import {createList, execute} from "$lib/graphql";
	import {goto} from "$app/navigation";

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

<h2 class="text-2xl mb-2 font-bold">Liste erstellen</h2>
<form onsubmit={handle_submit} class="w-full max-w-2xl gap-4 mb-5">
    <fieldset class="fieldset">
        <legend class="fieldset-legend">Name</legend>
        <input class="input" type="text" bind:value={form_state.name} required>
    </fieldset>

    <button class="btn btn-active btn-success" type="submit">Speichern</button>
</form>