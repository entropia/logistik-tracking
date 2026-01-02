<script lang="ts">
	import * as Field from "$lib/components/ui/field";
	import { Button } from "$lib/components/ui/button";
	import { QrCode, Search } from "@lucide/svelte";
	import { superForm } from "sveltekit-superforms";
    import type { PageProps } from "./$types";
    import GenericFormField from "$lib/components/ours/GenericFormField.svelte";
    import * as InputGroup from "$lib/components/ui/input-group";

    let {data}: PageProps = $props();

	let sf = superForm(data.form);

	let {form, enhance, tainted, isTainted} = sf;

    let id_input = $state<HTMLInputElement>(null!);
</script>

<svelte:head>
	<title>Resource finden</title>
</svelte:head>

<svelte:document
    onkeydown={(ev: KeyboardEvent) => {
        if (ev.key === "L" || ev.key === "l" || ev.key === "C" || ev.key === "c") id_input?.focus();
    }}
/>

<h2 class="text-2xl mb-5 font-bold">Resource finden</h2>

<p>Hier kannst du jegliche Kisten oder Listen einscannen, und zu deren seite kommen.</p>
<p>Am besten funktioniert das mit einem Barcodescanner.</p>

<form method="POST" class="w-full max-w-md mb-5 mt-5" use:enhance>
	<Field.Set>
		<GenericFormField superform={sf} field="id">
			{#snippet children({inpProps, labProps})}
                <Field.Label {...labProps}>ID (C123, L123, ...)</Field.Label>
                <InputGroup.Root>
                    <InputGroup.Input type="text" placeholder="ID hier" bind:value={$form.id} bind:ref={id_input} {...inpProps} />
                    <InputGroup.Addon>
                        <QrCode />
                    </InputGroup.Addon>
                </InputGroup.Root>
			{/snippet}
		</GenericFormField>

		<Field.Field>
			<Button type="submit" class="grow" disabled={!isTainted($tainted)}>
				<Search />
				Suchen
			</Button>
		</Field.Field>
	</Field.Set>
</form>
