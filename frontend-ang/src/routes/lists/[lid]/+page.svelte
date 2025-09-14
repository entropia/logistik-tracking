<script lang="ts">
	import type { PageProps } from './$types';
	import {DeliveryState} from "../../../gen/graphql";
	import {addCratesToList, execute, removeCratesFromList, updateListPacking} from "$lib/graphql";

	let {data}: PageProps = $props();

	let current = $state(data.list)

	let form_deliverystate = $state(current.deliveryStatet);

	let progShowing = $state(false);

	async function handle_submit(event: SubmitEvent) {
		event.preventDefault()
		progShowing = true;
		let resp = await execute(updateListPacking, fetch, {
			id: current.packingListId,
            newstate: form_deliverystate
		})
		let updated = resp.data?.setPackingListDeliveryState
		if (updated) {
			// display new info
            current.deliveryStatet = updated.deliveryStatet
			current.packedCrates = updated.packedCrates
		}
		progShowing = false;
	}

	async function run_del(ev: MouseEvent, id: string) {
		(ev.currentTarget as Element).setAttribute("disabled", "true")
		progShowing = true;
		let resp = await execute(removeCratesFromList, fetch, {
			pl: current.packingListId,
            crates: [id]
		})
		let updated = resp.data?.removeCratesFromPackingList
		if (updated) {
			// display new info
			current.packedCrates = updated.packedCrates
		} else {
			// something went wrong
			(ev.currentTarget as Element).removeAttribute("disabled")
        }
		progShowing = false;
    }

	async function add(id: string) {
		if (id.startsWith("C")) id = id.substring(1)
		progShowing = true;
		let resp = await execute(addCratesToList, fetch, {
			pl: current.packingListId,
			crates: [id]
		})
		let updated = resp.data?.addCratesToPackingList
		if (updated) {
			// display new info
			current.packedCrates = updated.packedCrates
		}
		progShowing = false;
    }

	let crate_id = $state<string>();

	async function add_crate(ev: SubmitEvent) {
		// jens war hier :)
		ev.preventDefault()
		await add(crate_id!!);
        (ev.target as HTMLFormElement).reset()
    }

	let id_input = $state<HTMLInputElement>();
</script>

<svelte:document onkeydown={(ev: KeyboardEvent) => {
    if (ev.key === "C") id_input?.focus()
}}></svelte:document>

<h2 class="text-2xl mb-2 font-bold">Liste {current.name} {#if progShowing}
    <span class="loading loading-spinner loading-sm"></span>
{/if}</h2>
<form onsubmit={handle_submit} class="w-full max-w-2xl mb-5">
    <fieldset class="fieldset">
        <legend class="fieldset-legend">Status</legend>
        <div class="flex flex-row gap-4">
            <select class="select w-full" bind:value={form_deliverystate} required>
                {#each Object.values(DeliveryState) as oc}
                    <option>{oc}</option>
                {/each}
            </select>
            <button type="submit" class="btn btn-active btn-success">Speichern</button>
        </div>
    </fieldset>
</form>

<h3 class="text-xl mb-2 mt-2">Kisten:</h3>

<form onsubmit={add_crate}>
    <fieldset class="fieldset">
        <legend class="fieldset-legend">Kisten-ID</legend>
        <div class="flex flex-row gap-4">
            <input type="text" class="input" placeholder="ID hier" required pattern="C?\d+" bind:value={crate_id} bind:this={id_input} />
            <button type="submit" class="btn btn-active btn-success">+</button>
        </div>
    </fieldset>
</form>

<table class="table table-auto" style="width: 100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>OC</th>
        <th>Name</th>
        <th>Wo?</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    {#each current.packedCrates as crate (crate.internalId)}
        <tr>
            <td>{crate.internalId}</td>
            <td>{crate.operationCenter}</td>
            <td>
                <a class="link" href="/crates/{crate.internalId}">{crate.name}</a>
            </td>
            <td>{crate.deliveryState}</td>
            <td>
                <button class="btn btn-error btn-sm" onclick={(ev) => run_del(ev, crate.internalId)}>Delete</button>
            </td>
        </tr>
    {/each}
    </tbody>
</table>