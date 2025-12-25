<script lang="ts">
    import type { SuperValidated, Infer } from "sveltekit-superforms";
    import { superForm } from "sveltekit-superforms";
    import type { CreateCrateType } from "$lib/schemas/crates";

    import * as Field from "$lib/components/ui/field";
    import * as InputGroup from "$lib/components/ui/input-group";
    import OperationCenterDropdown from "./OperationCenterDropdown.svelte";
    import DeliveryStateDropdown from "./DeliveryStateDropdown.svelte";
    import { Textarea } from "$lib/components/ui/textarea";
    import GenericFormField from "./GenericFormField.svelte";
    import type { HTMLAttributes } from "svelte/elements";
    import { Button } from "../ui/button";
    import { Save } from "@lucide/svelte";
    import { Input } from "../ui/input";
    import { Checkbox } from "../ui/checkbox";
    import { Label } from "../ui/label";
    
    let {
        data,
        ...onForm
    }: {
        data: SuperValidated<Infer<CreateCrateType>>;
    } & HTMLAttributes<HTMLFormElement> = $props();

    const sf = superForm(data, {
        resetForm: false,
        invalidateAll: false,
        onUpdated({form}) {
            if (form.data.createMultiple) {
                console.log(the_name)
                the_name?.focus()
            }
        }
    });

    const { form, enhance, isTainted, tainted, message } = sf;

	let the_name = $state<HTMLInputElement | null>(null);
</script>

{#if $message}
<p>{$message}</p>
{/if}

<form class="flex flex-col gap-5" method="POST" use:enhance {...onForm}>
    <Field.Group class="grid grid-cols-1 md:grid-cols-2">
        <GenericFormField superform={sf} field="operationCenter">
            {#snippet children({ labProps, inpProps })}
                <Field.Label {...labProps}>Operation Center</Field.Label>
                <OperationCenterDropdown bind:value={$form.operationCenter} {...inpProps}></OperationCenterDropdown>
            {/snippet}
        </GenericFormField>
        <GenericFormField superform={sf} field="name">
            {#snippet children({ labProps, inpProps })}
                <Field.Label {...labProps}>Name</Field.Label>
                <Input type="text" bind:value={$form.name} bind:ref={the_name} {...inpProps}></Input>
            {/snippet}
        </GenericFormField>
        <GenericFormField superform={sf} field="deliveryState">
            {#snippet children({ labProps, inpProps })}
                <Field.Label {...labProps}>Status</Field.Label>
                <DeliveryStateDropdown bind:value={$form.deliveryState} {...inpProps}></DeliveryStateDropdown>
            {/snippet}
        </GenericFormField>
        <GenericFormField superform={sf} field="jiraTicket">
            {#snippet children({ labProps, inpProps })}
                <Field.Label {...labProps}>Jira Ticket</Field.Label>
                <div class="flex flex-row gap-1">
                    <InputGroup.Root>
                        <InputGroup.Input bind:value={$form.jiraTicket} type="number" placeholder="1234" class="!ps-1" {...inpProps} />
                        <InputGroup.Addon>
                            <InputGroup.Text>LOC-</InputGroup.Text>
                        </InputGroup.Addon>
                    </InputGroup.Root>
                </div>
            {/snippet}
        </GenericFormField>
        <GenericFormField superform={sf} field="info" class="md:col-span-2">
            {#snippet children({ labProps, inpProps })}
                <Field.Label {...labProps}>Informationen</Field.Label>
                <Textarea bind:value={$form.info} placeholder="Information" {...inpProps}></Textarea>
            {/snippet}
        </GenericFormField>
    </Field.Group>

    <div class="flex flex-row gap-5">
        <Button type="submit" disabled={!isTainted($tainted)}>
            <Save />
            Speichern
        </Button>
        <div class="flex items-center gap-3">
            <Checkbox id="makeMultiple" name="createMultiple" bind:checked={$form.createMultiple} />
            <Label for="makeMultiple">Mehrere erstellen</Label>
        </div>
    </div>
</form>
