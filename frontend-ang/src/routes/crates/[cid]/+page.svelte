<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<p>Crate: {data.crates.name}</p>
<p>Info: {data.crates.information}</p>