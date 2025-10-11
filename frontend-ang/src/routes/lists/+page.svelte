<script lang="ts">
	import type { PageProps } from './$types';
	import {Query, SearcherFactory} from "@m31coding/fuzzy-search";
	import {DeliveryState} from "../../gen/graphql";

	type ListLike = {
		packingListId: string,
		name: string,
		deliveryStatet: DeliveryState
    };

	let {data}: PageProps = $props();
	// $inspect(data.lists);

	const searcher = SearcherFactory.createDefaultSearcher<ListLike, string>();

	function filterCrates(oc: ListLike[], f: string): ListLike[] {
		if (f == "") return oc;
		return searcher.getMatches(new Query(f)).matches.toSorted((am, bm) => bm.quality - am.quality).map(it => it.entity);
	}

	let filter = $state("");
	$effect(() => {
		console.log("reindexing crates")
		searcher.indexEntities(data.lists, x => x.packingListId, x => [
			x.name, x.deliveryStatet, x.packingListId
		])
		console.log(searcher)
	});
	let displayLists = $derived(filterCrates(data.lists, filter));
</script>

<h2 class="text-2xl mb-2 font-bold">Listen</h2>
<div class="flex flex-row gap-5 m-5">
    <a href="/lists/create" class="btn btn-info">Erstellen</a>
    <a href="/lists/masseneintragung" class="btn btn-info">Masseneintragung</a>
</div>

<input bind:value={filter} type="text" class="input" placeholder="Suchen..." maxlength="20">
<table class="table table-auto" style="width: 100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Wo?</th>
    </tr>
    </thead>
    <tbody>
    {#each displayLists as lis (lis.packingListId)}
        <tr>
            <td>{lis.packingListId}</td>
            <td>
                <a class="link" href="/lists/{lis.packingListId}">{lis.name}</a>
            </td>
            <td>{lis.deliveryStatet}</td>
        </tr>
<!--        <p>Crate {crate.internalId}: {crate.name}</p>-->
    {/each}
    </tbody>
</table>

<!--<p>Count crates: {data.crates.getEuroCrates.length}</p>-->
<!--{#each data.crates.getEuroCrates as crate (crate.internalId)}-->
<!--    <p>Crate {crate.internalId}: {crate.name}</p>-->
<!--{/each}-->