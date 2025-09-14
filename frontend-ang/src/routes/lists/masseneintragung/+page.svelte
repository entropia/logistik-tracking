<script lang="ts">
    import {DeliveryState} from "../../../gen/graphql";
	import {execute, updateListPacking} from "$lib/graphql";

	let list_id = $state("");
	let id_input = $state<HTMLInputElement>();
	let form_deliverystate = $state(DeliveryState.AtGpn);
	let feedback = $state<HTMLParagraphElement>();

	async function do_update(ev: SubmitEvent) {
		ev.preventDefault()
        let theid = list_id;
		if (theid.startsWith("L")) theid = theid.substring(1);
        let ret = await execute(updateListPacking, window.fetch, {
            id:theid,
            newstate: form_deliverystate
        });
		list_id = ""
        feedback!.textContent = `Liste ${theid} / ${ret.data!!.setPackingListDeliveryState!!.name} hat jetzt Status ${form_deliverystate}`;
    }
</script>

<svelte:document onkeydown={(ev: KeyboardEvent) => {
    if (ev.key === "L") id_input?.focus()
}}></svelte:document>

<form onsubmit={do_update} class="w-full max-w-2xl mb-5">
    <fieldset class="fieldset">
        <legend class="fieldset-legend">Neuer Status</legend>
        <select class="select w-full" bind:value={form_deliverystate} required>
            {#each Object.values(DeliveryState) as oc}
                <option>{oc}</option>
            {/each}
        </select>
    </fieldset>

    <fieldset class="fieldset">
        <legend class="fieldset-legend">Listen-ID</legend>
        <input type="text" class="input w-full" placeholder="ID hier" required pattern="L?\d+" bind:value={list_id} bind:this={id_input} />
    </fieldset>

    <button type="submit" class="btn btn-active btn-success mt-2">Aktualisieren</button>
</form>

<p bind:this={feedback} class="mt-4"></p>