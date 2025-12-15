<script lang="ts">
	import {DeliveryState, OperationCenter} from "../../../gen/graphql";
	import {createCrate, execute} from "$lib/graphql";
	import * as Field from "$lib/components/ui/field";
	import * as Select from "$lib/components/ui/select";
	import * as InputGroup from "$lib/components/ui/input-group";
	import {goto} from "$app/navigation";
	import {Input} from "$lib/components/ui/input";
	import {Textarea} from "$lib/components/ui/textarea";
	import {Button} from "$lib/components/ui/button";
	import {Checkbox} from "$lib/components/ui/checkbox";
	import {Label} from "$lib/components/ui/label";
	import {toast} from "svelte-sonner";
	import OperationCenterDropdown from "$lib/components/ours/OperationCenterDropdown.svelte";
	import DeliveryStateDropdown from "$lib/components/ours/DeliveryStateDropdown.svelte";
    import { Save } from "@lucide/svelte";

	let create_multiple = $state(false);

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
		const creatingId = toast.loading("Kiste wird erstellt...")
		let resp = await execute(createCrate, fetch, {
			info: form_state.info,
            oc: form_state.oc,
            deli: form_state.deli,
            name: form_state.name,
            jira: form_state.jira ? ("LOC-"+form_state.jira) : null
		})
		let updated = resp.data?.createEuroCrate
		if (updated) {
			toast.dismiss(creatingId)
			toast.success(`Kiste ${form_state.oc} / ${form_state.name} erstellt: ${updated.internalId}`)
			if (!create_multiple) await goto("/crates/"+updated.internalId);
			else {
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
</script>

<h2 class="text-2xl mb-5 font-bold">Box erstellen</h2>
<form onsubmit={handle_submit} class="w-full max-w-3xl mb-5 flex flex-col gap-5">
    <Field.Group class="grid grid-cols-1 md:grid-cols-2">
        <Field.Field>
            <Field.Label for="operationcenter">Operation Center</Field.Label>
            <OperationCenterDropdown id="operationcenter" bind:value={form_state.oc}></OperationCenterDropdown>
        </Field.Field>
        <Field.Field>
            <Field.Label for="name">Name</Field.Label>
            <Input type="text" id="name" bind:value={form_state.name} bind:ref={the_name} required></Input>
        </Field.Field>
        <Field.Field>
            <Field.Label for="status">Status</Field.Label>
            <DeliveryStateDropdown id="status" bind:value={form_state.deli}></DeliveryStateDropdown>
        </Field.Field>
        <Field.Field>
            <Field.Label for="ticket">Jira Ticket</Field.Label>
            <InputGroup.Root>
                <InputGroup.Input type="number" bind:value={form_state.jira} min="1" placeholder="1234" id="ticket" class="!ps-1" />
                <InputGroup.Addon>
                    <InputGroup.Text>LOC-</InputGroup.Text>
                </InputGroup.Addon>
            </InputGroup.Root>
        </Field.Field>
        <Field.Field class="md:col-span-2">
            <Field.Label for="infos">Informationen</Field.Label>
            <Textarea bind:value={form_state.info} placeholder="Information"></Textarea>
        </Field.Field>
    </Field.Group>

    <div class="flex flex-row gap-5">
        <Button type="submit">
            <Save />
            Speichern
        </Button>

        <div class="flex items-center gap-3">
            <Checkbox id="makeMultiple" bind:checked={create_multiple} />
            <Label for="makeMultiple">Mehrere erstellen</Label>
        </div>
    </div>
</form>