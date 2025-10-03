<script lang="ts">
	import {DeliveryState, OperationCenter} from "../../../gen/graphql";
	import {createCrate, execute} from "$lib/graphql";
	import {goto} from "$app/navigation";

	let form_state = $state({
        name: "",
        oc: OperationCenter.Loc,
        deli: DeliveryState.Packing,
        info: "",
        jira: ""
    });
	async function handle_submit(event: SubmitEvent) {
		event.preventDefault()
		let resp = await execute(createCrate, fetch, {
			info: form_state.info,
            oc: form_state.oc,
            deli: form_state.deli,
            name: form_state.name,
            jira: form_state.jira
		})
		let updated = resp.data?.createEuroCrate
		if (updated) {
			await goto("/crates/"+updated.internalId);
		}
	}
</script>

<h2 class="text-2xl mb-2 font-bold">Box erstellen</h2>
<form onsubmit={handle_submit} class="grid grid-cols-2 grid-rows-1 w-full max-w-2xl gap-4 mb-5">
    <fieldset class="fieldset">
        <legend class="fieldset-legend">Operation Center</legend>
        <select class="select w-full" bind:value={form_state.oc} required>
            {#each Object.values(OperationCenter) as oc}
                <option>{oc}</option>
            {/each}
        </select>
    </fieldset>
    <fieldset class="fieldset">
        <legend class="fieldset-legend">Name</legend>
        <input class="input" type="text" bind:value={form_state.name} required>
    </fieldset>

    <fieldset class="fieldset">
        <legend class="fieldset-legend">Status</legend>
        <select class="select w-full" bind:value={form_state.deli} required>
            {#each Object.values(DeliveryState) as oc}
                <option>{oc}</option>
            {/each}
        </select>
    </fieldset>

    <fieldset class="fieldset">
        <legend class="fieldset-legend">Jira Ticket</legend>
        <input class="input" type="text" bind:value={form_state.jira} pattern="^LOC-\d+$" placeholder="LOC-...">
    </fieldset>

    <fieldset class="fieldset col-span-2">
        <legend class="fieldset-legend">Information</legend>
        <textarea class="textarea w-full" bind:value={form_state.info} placeholder="Information"></textarea>
    </fieldset>

    <button class="btn btn-active btn-success" type="submit">Speichern</button>
</form>