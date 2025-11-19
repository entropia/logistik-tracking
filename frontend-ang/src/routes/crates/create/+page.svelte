<script lang="ts">
	import {DeliveryState, OperationCenter} from "../../../gen/graphql";
	import {createCrate, execute} from "$lib/graphql";
	import {goto} from "$app/navigation";

	let create_multiple = $state(false);

	let the_status = $state<HTMLParagraphElement | null>(null);
	let the_name = $state<HTMLInputElement | null>(null);

	let form_state = $state({
        name: "",
        oc: OperationCenter.Loc,
        deli: DeliveryState.Packing,
        info: "",
        jira: undefined
    });
	async function handle_submit(event: SubmitEvent) {
		event.preventDefault()
		let resp = await execute(createCrate, fetch, {
			info: form_state.info,
            oc: form_state.oc,
            deli: form_state.deli,
            name: form_state.name,
            jira: form_state.jira ? ("LOC-"+form_state.jira) : null
		})
		let updated = resp.data?.createEuroCrate
		if (updated) {
			if (!create_multiple) await goto("/crates/"+updated.internalId);
			else {
				if (the_status) {
					the_status.textContent = `Kiste ${form_state.oc} / ${form_state.name} erstellt: ${updated.internalId}`;
					// reset geht hier leider nicht weil die checkbox in der form ist und damit auch zurückgesetzt wird
					form_state.name = ""
                    form_state.oc = OperationCenter.Loc;
					form_state.deli = DeliveryState.Packing;
					form_state.info = "";
					form_state.jira = undefined;
                    the_name?.focus()
                }
            }
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
        <input class="input" type="text" bind:value={form_state.name} bind:this={the_name} required>
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
        <label class="input">
            LOC-
            <input class="grow" type="number" bind:value={form_state.jira} min="1" placeholder="1234">
        </label>
    </fieldset>

    <fieldset class="fieldset col-span-2">
        <legend class="fieldset-legend">Information</legend>
        <textarea class="textarea w-full" bind:value={form_state.info} placeholder="Information"></textarea>
    </fieldset>

    <button class="btn btn-active btn-success" type="submit">Speichern</button>

    <label class="label">
        <input type="checkbox" bind:checked={create_multiple}>
        Mehrere erstellen
    </label>

    <p bind:this={the_status}></p>
</form>