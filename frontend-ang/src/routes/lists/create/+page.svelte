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
<!--todo: fix for shadcn-->
<h2 class="text-2xl mb-2 font-bold">Liste erstellen</h2>
<form onsubmit={handle_submit} class="grid grid-cols-2 grid-rows-1 w-full max-w-2xl gap-4 mb-5">
    <fieldset class="fieldset col-span-1">
        <legend class="fieldset-legend">Name</legend>
        <input class="input w-full" type="text" bind:value={form_state.name} required>
    </fieldset>

    <button class="btn btn-active btn-success col-start-1" type="submit">Speichern</button>
</form>