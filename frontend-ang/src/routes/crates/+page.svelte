<script lang="ts">
	import type { PageProps } from './$types';
	import CratesDisplay from "../../components/CratesDisplay.svelte";
	import {Button} from "$lib/components/ui/button/index";

	let {data}: PageProps = $props();

</script>

<h2 class="text-2xl mb-2 font-bold">Kisten</h2>
<div class="flex flex-row gap-5 m-5 text-inherit">
    <Button href="/crates/create">
        Erstellen
    </Button>
</div>



<CratesDisplay crates={data.crates.getEuroCrates}>

</CratesDisplay>