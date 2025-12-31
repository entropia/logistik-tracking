<script lang="ts">
	import * as Field from "$lib/components/ui/field";
	import { Checkbox } from "$lib/components/ui/checkbox";
	import { Button } from "$lib/components/ui/button";
	import { Input } from "$lib/components/ui/input";
	import { Save } from "@lucide/svelte";
	import * as Select from "$lib/components/ui/select";
    import SuperDebug, { superForm } from "sveltekit-superforms";
    import type { PageProps } from "./$types";
    import { allowedAuthorities } from "$lib/schemas/users";
    import GenericFormField from "$lib/components/ours/GenericFormField.svelte";

	const mapping: Record<(typeof allowedAuthorities)[number], string> = {
		MANAGE_RESOURCES: "Ressourcen verwalten",
		MANAGE_USERS: "Nutzer verwalten",
		PRINT: "Drucken",
	};
	
	let {data}: PageProps = $props();

	let sf = superForm(data.form, {
		invalidateAll: false
	});

	let {form, enhance, constraints, errors} = sf;
</script>

<svelte:head>
	<title>Nutzer erstellen</title>
</svelte:head>

<SuperDebug data={form}></SuperDebug>

<h2 class="text-2xl mb-5 font-bold">Nutzer erstellen</h2>

<form method="POST" class="w-full max-w-md mb-5" use:enhance>
	<Field.Set>
		<GenericFormField superform={sf} field="active" orientation="horizontal">
			{#snippet children({inpProps, labProps})}
				<Checkbox bind:checked={$form.active} {...inpProps} required={false}></Checkbox>
				<Field.Label {...labProps}>Aktiviert?</Field.Label>
			{/snippet}
		</GenericFormField>

		<GenericFormField superform={sf} field="name">
			{#snippet children({inpProps, labProps})}
				<Field.Label {...labProps}>Nutzername ([a-zA-Z0-9_\-]+)</Field.Label>
				<Input placeholder="Nutzername" bind:value={$form.name} {...inpProps}></Input>
			{/snippet}
		</GenericFormField>
		<GenericFormField superform={sf} field="password">
			{#snippet children({inpProps, labProps})}
				<Field.Label {...labProps}>Passwort (min 8 chars)</Field.Label>
				<Input placeholder="Passwort" type="password" bind:value={$form.password} {...inpProps}></Input>
			{/snippet}
		</GenericFormField>
		<Field.Field>
			<!-- arrays und so funktionieren leider nicht mit genericformfield -->
			<Field.Label for="authorities">Berechtigungen</Field.Label>
			<Select.Root type="multiple" bind:value={$form.authorities} name="authorities">
				<Select.Trigger id="authorities" class="w-full">
					{$form.authorities.map(it => mapping[it]).join(", ") || "Auswählen..."}
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
			<Button type="submit" class="grow">
				<Save />
				Speichern
			</Button>
		</Field.Field>
	</Field.Set>
</form>
