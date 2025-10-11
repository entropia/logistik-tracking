<script lang="ts">
	import type { PageProps } from './$types';
	import {DeliveryState, OperationCenter} from "../../../gen/graphql";
	import {execute, updateCrate} from "$lib/graphql";

	let {data}: PageProps = $props();

	let current_state = $state(data.crates)

	let the = $state({
        oc: current_state.operationCenter,
        status: current_state.deliveryState,
        info: current_state.information,
        jira: current_state.jiraId
    })

    let progShowing = $state(false);

    async function handle_submit(event: SubmitEvent) {
		event.preventDefault()
        progShowing = true;
        let resp = await execute(updateCrate, fetch, {
			    which: current_state.internalId,
            deli: the.status,
            info: the.info,
            oc: the.oc,
            jira: the.jira
            })
        let updated = resp.data?.modifyEuroCrate
        if (updated) {
			// display new info
            current_state.operationCenter = updated.operationCenter;
			current_state.deliveryState = updated.deliveryState;
			current_state.information = updated.information;
			current_state.jiraId = updated.jiraId;
        }
		progShowing = false;
    }
</script>

<h2 class="text-2xl mb-2 font-bold">Box {current_state.operationCenter} / {current_state.name}</h2>
<form onsubmit={handle_submit} class="grid grid-cols-2 grid-rows-1 w-full max-w-2xl gap-4 mb-5">
    <fieldset class="fieldset">
        <legend class="fieldset-legend">Operation Center</legend>
        <select class="select w-full" bind:value={the.oc} required>
            {#each Object.values(OperationCenter) as oc}
                <option>{oc}</option>
            {/each}
        </select>
    </fieldset>

    <fieldset class="fieldset">
        <legend class="fieldset-legend">Status</legend>
        <select class="select w-full" bind:value={the.status} required>
            {#each Object.values(DeliveryState) as oc}
                <option>{oc}</option>
            {/each}
        </select>
    </fieldset>

    <fieldset class="fieldset">
        <legend class="fieldset-legend">Jira Ticket</legend>
        <input class="input" type="text" bind:value={the.jira} pattern="^LOC-\d+$" placeholder="LOC-...">
    </fieldset>

    <fieldset class="fieldset col-span-2">
        <legend class="fieldset-legend">Information</legend>
        <textarea class="textarea w-full" bind:value={the.info} placeholder="Information"></textarea>
    </fieldset>

    <button class="btn btn-active btn-success" type="submit">Speichern
        {#if progShowing}
            <span class="loading loading-spinner loading-sm"></span>
            {/if}</button>
</form>

{#if current_state.packingList}
    <a class="text-xl mt-4 mb-2 link font-medium" href="/lists/{current_state.packingList.packingListId}">Zugehörige Liste {current_state.packingList.packingListId}</a>
    <p>Name: {current_state.packingList.name}</p>
    <p>Status: {current_state.packingList.deliveryStatet}</p>
{/if}
