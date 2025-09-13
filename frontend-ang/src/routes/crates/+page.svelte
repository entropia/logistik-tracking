<script lang="ts">
	import type { PageData } from './$types';
	import CratesDisplay from "../../components/CratesDisplay.svelte";

	export let data: PageData;
</script>

<h2 class="text-2xl mb-2 font-bold">Kisten</h2>

<CratesDisplay crates={data.crates.getEuroCrates}>

</CratesDisplay>