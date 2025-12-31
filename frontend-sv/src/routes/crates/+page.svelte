<script lang="ts">
	import type { PageProps } from './$types';
	import {Button} from "$lib/components/ui/button/index";
	import CratesDisplay from "$lib/components/ours/CratesDisplay.svelte";
    import { Plus } from '@lucide/svelte';

	let {data}: PageProps = $props();

</script>

<svelte:head>
    <title>Kisten</title>
</svelte:head>

<h2 class="text-2xl mb-2 font-bold">Kisten</h2>
<div class="flex flex-row gap-5 m-5 text-inherit">
    <Button href="/crates/create">
		<Plus />
        Erstellen
    </Button>
</div>

<CratesDisplay crates={data.crates}>

</CratesDisplay>