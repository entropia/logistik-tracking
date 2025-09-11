<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<table class="table">
    <thead>
    <tr>
        <th>ID</th>
        <th>OC</th>
        <th>Name</th>
        <th>Wo?</th>
    </tr>
    </thead>
    <tbody>
    {#each data.crates.getEuroCrates as crate (crate.internalId)}
        <tr>
            <td>{crate.internalId}</td>
            <td>{crate.operationCenter}</td>
            <td>
                <a class="link" href="/crates/{crate.internalId}">{crate.name}</a>
            </td>
            <td>{crate.deliveryState}</td>
        </tr>
<!--        <p>Crate {crate.internalId}: {crate.name}</p>-->
    {/each}
    </tbody>
</table>

<!--<p>Count crates: {data.crates.getEuroCrates.length}</p>-->
<!--{#each data.crates.getEuroCrates as crate (crate.internalId)}-->
<!--    <p>Crate {crate.internalId}: {crate.name}</p>-->
<!--{/each}-->