<script lang="ts">
	import {DeliveryState, OperationCenter} from "../../../gen/graphql";
	import {execute, updateListPacking} from "$lib/graphql";
	import {prepare_id} from "$lib/id_parser";
	import * as Field from "$lib/components/ui/field";
	import * as Select from "$lib/components/ui/select";
	import * as InputGroup from "$lib/components/ui/input-group";
	import {Input} from "$lib/components/ui/input";
	import {Button} from "$lib/components/ui/button";

	let list_id = $state("");
	let id_input = $state<HTMLInputElement>(null!);
	let form_deliverystate = $state(DeliveryState.AtGpn);
	let feedback = $state<HTMLParagraphElement>();

	async function do_update(ev: SubmitEvent) {
		ev.preventDefault()
        let theid = prepare_id(list_id);
        let ret = await execute(updateListPacking, window.fetch, {
            id:theid,
            newstate: form_deliverystate
        });
		list_id = ""
        feedback!.textContent = `Liste ${theid} / ${ret.data!!.setPackingListDeliveryState!!.name} hat jetzt Status ${form_deliverystate}`;
    }
</script>

<svelte:document onkeydown={(ev: KeyboardEvent) => {
    if (ev.key === "L" || ev.key === "l") id_input?.focus()
}}></svelte:document>

<h2 class="text-2xl mb-5 font-bold">Masseneintragung</h2>
<p>Hier kannst du viele Listen kontinuierlich auf einen bestimmten Status setzen.</p>
<p>Am besten funktioniert das mit einem Barcodescanner. Wenn ein Barcode erkannt wird, wird er automatisch eingetragen.</p>
<p>Wenn du nur eine bestimmte Liste ändern willst bist du <a class="link" href="/lists">woanders</a> besser aufgehoben.</p>

<form onsubmit={do_update} class="w-full max-w-2xl mb-5 mt-5">
    <Field.Group>
        <Field.Field>
            <Field.Label for="operationcenter">Neuer Status</Field.Label>
            <Select.Root type="single" bind:value={form_deliverystate}>
                <Select.Trigger id="operationcenter">{form_deliverystate}</Select.Trigger>
                <Select.Content>
                    {#each Object.values(OperationCenter) as oc}
                        <Select.Item value={oc}>{oc}</Select.Item>
                    {/each}
                </Select.Content>
            </Select.Root>
        </Field.Field>
        <Field.Field>
            <Field.Label for="lid">Listen-ID</Field.Label>
            <Input type="text" placeholder="ID hier" required pattern="[lL]?\d+" bind:value={list_id} bind:ref={id_input}></Input>
        </Field.Field>
        <Button type="submit">Aktualisieren</Button>
    </Field.Group>
</form>

<p bind:this={feedback} class="mt-4"></p>