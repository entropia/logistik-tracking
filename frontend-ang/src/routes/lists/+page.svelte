<script lang="ts">
	import type { PageData } from './$types';

	export let data: PageData;
</script>

<h2 class="text-2xl mb-2 font-bold">Listen</h2>
<div class="flex flex-row gap-5 m-5">
    <a href="/lists/create" class="btn btn-info">Erstellen</a>
    <a href="/lists/masseneintragung" class="btn btn-info">Masseneintragung</a>
</div>

<table class="table table-auto" style="width: 100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Wo?</th>
    </tr>
    </thead>
    <tbody>
    {#each data.lists.getPackingLists as crate (crate.packingListId)}
        <tr>
            <td>{crate.packingListId}</td>
            <td>
                <a class="link" href="/lists/{crate.packingListId}">{crate.name}</a>
            </td>
            <td>{crate.deliveryStatet}</td>
        </tr>
<!--        <p>Crate {crate.internalId}: {crate.name}</p>-->
    {/each}
    </tbody>
</table>

<!--<p>Count crates: {data.crates.getEuroCrates.length}</p>-->
<!--{#each data.crates.getEuroCrates as crate (crate.internalId)}-->
<!--    <p>Crate {crate.internalId}: {crate.name}</p>-->
<!--{/each}-->