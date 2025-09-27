<script lang="ts">
	import type { PageData } from './$types';
	import CratesDisplay from "../../components/CratesDisplay.svelte";

	export let data: PageData;
</script>

<h2 class="text-2xl mb-2 font-bold">Kisten</h2>
<div class="flex flex-row gap-5 m-5">
    <a href="/crates/create" class="btn btn-info">Erstellen</a>
</div>


<CratesDisplay crates={data.crates.getEuroCrates}>

</CratesDisplay>