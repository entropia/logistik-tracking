<script lang="ts">
	import type { PageProps } from './$types';
	import * as Field from "$lib/components/ui/field";
	import * as InputGroup from "$lib/components/ui/input-group";
	import {execute, updateCrate} from "$lib/graphql";
	import {Textarea} from "$lib/components/ui/textarea";
	import {Button} from "$lib/components/ui/button";
	import {toast} from "svelte-sonner";
	import OperationCenterDropdown from "$lib/components/ours/OperationCenterDropdown.svelte";
	import DeliveryStateDropdown from "$lib/components/ours/DeliveryStateDropdown.svelte";
    import { Save } from '@lucide/svelte';

	let {data}: PageProps = $props();

	let current_state = $state(data.crates)

	let the = $state({
        oc: current_state.operationCenter,
        status: current_state.deliveryState,
        info: current_state.information,
        jira: current_state.jiraId ? parseInt(current_state.jiraId.substring(4)) : undefined
    })

    async function handle_submit(event: SubmitEvent) {
		event.preventDefault()
        let resp = await execute(updateCrate, fetch, {
			    which: current_state.internalId,
            deli: the.status,
            info: the.info,
            oc: the.oc,
            jira: the.jira ? "LOC-"+the.jira : undefined
            })
        let updated = resp.data?.modifyEuroCrate
        if (updated) {
			// display new info
            current_state.operationCenter = updated.operationCenter;
			current_state.deliveryState = updated.deliveryState;
			current_state.information = updated.information;
			current_state.jiraId = updated.jiraId;
			toast.success("Kiste Aktualisiert!")
        }
    }
</script>

<svelte:head>
    <title>Kiste {current_state.operationCenter} / {current_state.name}</title>
</svelte:head>

<h2 class="text-2xl mb-5 font-bold">Box {current_state.operationCenter} / {current_state.name}</h2>

<form onsubmit={handle_submit} class="w-full max-w-3xl mb-5 flex flex-col gap-5">
    <Field.Group class="grid grid-cols-1 md:grid-cols-2">
        <Field.Field>
            <Field.Label for="operationcenter">Operation Center</Field.Label>
            <OperationCenterDropdown id="operationcenter" bind:value={the.oc}></OperationCenterDropdown>
        </Field.Field>
        <Field.Field>
            <Field.Label for="status">Status</Field.Label>
            <DeliveryStateDropdown id="status" bind:value={the.status}></DeliveryStateDropdown>
        </Field.Field>
        <Field.Field>
            <Field.Label for="ticket">Jira Ticket</Field.Label>
            <InputGroup.Root>
                <InputGroup.Input type="number" bind:value={the.jira} min="1" placeholder="1234" id="ticket" class="!ps-1" />
                <InputGroup.Addon>
                    <InputGroup.Text>LOC-</InputGroup.Text>
                </InputGroup.Addon>
            </InputGroup.Root>
        </Field.Field>
        <Field.Field class="md:col-span-2">
            <Field.Label for="infos">Informationen</Field.Label>
            <Textarea bind:value={the.info} placeholder="Information"></Textarea>
        </Field.Field>
    </Field.Group>


    <div class="flex flex-row gap-5">
        <Button type="submit">
            <Save />
            Speichern
        </Button>
    </div>
</form>

{#if current_state.packingList}
    <a class="text-xl mt-4 mb-2 link font-medium" href="/lists/{current_state.packingList.packingListId}">
        Zugehörige Liste {current_state.packingList.packingListId}
    </a>
    <p>Name: {current_state.packingList.name}</p>
    <p>Status: {current_state.packingList.deliveryStatet}</p>
{/if}
