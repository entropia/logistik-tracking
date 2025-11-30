<script lang="ts">
	import type { PageProps } from './$types';
	import * as Field from "$lib/components/ui/field";
	import * as Select from "$lib/components/ui/select";
	import * as InputGroup from "$lib/components/ui/input-group";
	import {DeliveryState, OperationCenter} from "../../../gen/graphql";
	import {execute, updateCrate} from "$lib/graphql";
	import {Input} from "$lib/components/ui/input";
	import {Textarea} from "$lib/components/ui/textarea";
	import {Button} from "$lib/components/ui/button";
	import {Label} from "$lib/components/ui/label";
	import {toast} from "svelte-sonner";

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

<h2 class="text-2xl mb-5 font-bold">Box {current_state.operationCenter} / {current_state.name}</h2>

<form onsubmit={handle_submit} class="w-full max-w-3xl mb-5 flex flex-col gap-5">
    <Field.Group class="grid grid-cols-1 md:grid-cols-2">
        <Field.Field>
            <Field.Label for="operationcenter">Operation Center</Field.Label>
            <Select.Root type="single" bind:value={the.oc}>
                <Select.Trigger id="operationcenter">{the.oc}</Select.Trigger>
                <Select.Content>
                    {#each Object.values(OperationCenter) as oc}
                        <Select.Item value={oc}>{oc}</Select.Item>
                    {/each}
                </Select.Content>
            </Select.Root>
        </Field.Field>
        <Field.Field>
            <Field.Label for="status">Status</Field.Label>
            <Select.Root type="single" bind:value={the.status}>
                <Select.Trigger id="status">{the.status}</Select.Trigger>
                <Select.Content>
                    {#each Object.values(DeliveryState) as oc}
                        <Select.Item value={oc}>{oc}</Select.Item>
                    {/each}
                </Select.Content>
            </Select.Root>
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
        <Button type="submit">Speichern</Button>
    </div>
</form>

{#if current_state.packingList}
    <a class="text-xl mt-4 mb-2 link font-medium" href="/lists/{current_state.packingList.packingListId}">Zugehörige Liste {current_state.packingList.packingListId}</a>
    <p>Name: {current_state.packingList.name}</p>
    <p>Status: {current_state.packingList.deliveryStatet}</p>
{/if}
