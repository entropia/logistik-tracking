<script lang="ts">
    import { DeliveryState } from "$lib/../gen/graphql";
    import { execute, updateListPacking } from "$lib/graphql";
    import { prepare_id } from "$lib/id_parser";
    import * as Field from "$lib/components/ui/field";
    import * as InputGroup from "$lib/components/ui/input-group";
    import { Button } from "$lib/components/ui/button";
    import DeliveryStateDropdown from "$lib/components/ours/DeliveryStateDropdown.svelte";
    import { QrCode } from "@lucide/svelte";
    import { superForm } from "sveltekit-superforms";
    import type { PageProps } from "./$types";
    import GenericFormField from "$lib/components/ours/GenericFormField.svelte";

    let id_input = $state<HTMLInputElement>(null!);

    let { data }: PageProps = $props();

    let sf = superForm(data.form, {
        resetForm: false,
        invalidateAll: false,
        onUpdate({form}) {
            if (form.valid) {
                // wir sind durch
                form.data.id = "";
            }
        }
    });

    let { form, enhance, tainted, isTainted, message } = sf;
</script>

<svelte:head>
    <title>Listen Masseneintragung</title>
</svelte:head>

<svelte:document
    onkeydown={(ev: KeyboardEvent) => {
        if (ev.key === "L" || ev.key === "l") id_input?.focus();
    }}
/>

<h2 class="text-2xl mb-5 font-bold">Masseneintragung</h2>
<p>Hier kannst du viele Listen kontinuierlich auf einen bestimmten Status setzen.</p>
<p>Am besten funktioniert das mit einem Barcodescanner. Wenn ein Barcode erkannt wird, wird er automatisch eingetragen.</p>
<p>Wenn du nur eine bestimmte Liste ändern willst bist du <a class="link" href="/lists">woanders</a> besser aufgehoben.</p>

<form method="POST" use:enhance class="w-full max-w-2xl mb-5 mt-5">
    <Field.Group>
        <GenericFormField superform={sf} field="deliveryState">
            {#snippet children({ inpProps, labProps })}
                <Field.Label {...labProps}>Neuer Status</Field.Label>
                <DeliveryStateDropdown bind:value={$form.deliveryState} {...inpProps}></DeliveryStateDropdown>
            {/snippet}
        </GenericFormField>
        <GenericFormField superform={sf} field="id">
            {#snippet children({ inpProps, labProps })}
                <Field.Label {...labProps}>Listen-ID (L...)</Field.Label>
                <InputGroup.Root>
                    <InputGroup.Input type="text" placeholder="ID hier" bind:value={$form.id} bind:ref={id_input} {...inpProps} />
                    <InputGroup.Addon>
                        <QrCode />
                    </InputGroup.Addon>
                </InputGroup.Root>
            {/snippet}
        </GenericFormField>
        <Button type="submit" disabled={!isTainted($tainted)}>Aktualisieren</Button>
    </Field.Group>
</form>

{#if $message}
<p class="mt-4">{$message}</p>
{/if}