<script lang="ts">
	import type { PageProps } from './$types';
	import CratesDisplay from "../../components/CratesDisplay.svelte";

	let {data}: PageProps = $props();

</script>

<h2 class="text-2xl mb-2 font-bold">Kisten</h2>
<div class="flex flex-row gap-5 m-5">
    <a href="/crates/create" class="btn btn-info">
        <span class="icon-[material-symbols--add]" style="width: 24px; height: 24px;"></span>
        Erstellen
    </a>
</div>



<CratesDisplay crates={data.crates.getEuroCrates}>

</CratesDisplay>