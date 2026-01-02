<script lang="ts">
    import type { PageProps } from "./$types";
    import * as Field from "$lib/components/ui/field";
    import { Checkbox } from "$lib/components/ui/checkbox";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Save } from "@lucide/svelte";
    import { superForm } from "sveltekit-superforms";
    import GenericFormField from "$lib/components/ours/GenericFormField.svelte";
    import * as Select from "$lib/components/ui/select";

    let { data }: PageProps = $props();

    let sf = superForm(data.form, {
        resetForm: false,
        invalidateAll: false, // durch das update kann nur der inhalt der form aktualisiert werden. ein refetch ist nicht notwendig
    });

    let { form, enhance, tainted, isTainted, message, errors } = sf;

    const allowedAuthorities = ["MANAGE_RESOURCES", "MANAGE_USERS", "PRINT"] as const;
    const mapping: Record<(typeof allowedAuthorities)[number], string> = {
        MANAGE_RESOURCES: "Ressourcen verwalten",
        MANAGE_USERS: "Nutzer verwalten",
        PRINT: "Drucken",
    };
</script>

<svelte:head>
    <title>Nutzer {data.user.username}</title>
</svelte:head>

<h2 class="text-2xl mb-5 font-bold">Nutzer {data.user.username}</h2>

<form method="POST" use:enhance class="w-full max-w-md mb-5">
    <Field.Set>
        <GenericFormField superform={sf} field="active" orientation="horizontal">
            {#snippet children({ inpProps, labProps })}
                <Checkbox bind:checked={$form.active} {...inpProps} required={false}></Checkbox>
                <Field.Label {...labProps}>Aktiviert?</Field.Label>
            {/snippet}
        </GenericFormField>

        <GenericFormField superform={sf} field="password">
            {#snippet children({ inpProps, labProps })}
                <Field.Label {...labProps}>Neues Passwort (min 8 chars)</Field.Label>
                <Input placeholder="Passwort (ignoriert wenn leer)" type="password" bind:value={$form.password} {...inpProps}></Input>
            {/snippet}
        </GenericFormField>

        <Field.Field>
            <!-- arrays und so funktionieren leider nicht mit genericformfield -->
            <Field.Label for="authorities">Berechtigungen</Field.Label>
            <Select.Root type="multiple" bind:value={$form.authorities} name="authorities">
                <Select.Trigger id="authorities" class="w-full">
                    {$form.authorities.map((it) => mapping[it]).join(", ") || "Auswählen..."}
                </Select.Trigger>
                <Select.Content>
                    {#each allowedAuthorities as authority (authority)}
                        <Select.Item value={authority}>{mapping[authority]}</Select.Item>
                    {/each}
                </Select.Content>
            </Select.Root>
            {#if $errors.authorities}
                <Field.Error>{$errors.authorities}</Field.Error>
            {/if}
        </Field.Field>

        <Field.Field>
            <Button type="submit" class="grow" disabled={!isTainted($tainted)}>
                <Save />
                Speichern
            </Button>
        </Field.Field>
    </Field.Set>
</form>

<p>{$message}</p>
