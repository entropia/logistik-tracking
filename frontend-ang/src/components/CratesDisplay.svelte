<script lang="ts">
	import {DeliveryState, OperationCenter} from "../gen/graphql";
	import {Query, SearcherFactory} from '@m31coding/fuzzy-search';

	type CrateLike = {
		deliveryState: DeliveryState;
		internalId: string;
		name: string;
		operationCenter: OperationCenter;
	};

	const searcher = SearcherFactory.createDefaultSearcher<CrateLike, string>();

	function filterCrates(oc: CrateLike[], f: string): CrateLike[] {
		if (f == "") return oc;
		return searcher.getMatches(new Query(f)).matches.toSorted((am, bm) => bm.quality - am.quality).map(it => it.entity);
    }

	let {crates}: {crates: CrateLike[]} = $props();
	let filter = $state("");
	$effect(() => {
		console.log("reindexing crates")
		searcher.indexEntities(crates, x => x.internalId, x => [
			x.operationCenter, x.name, `${x.operationCenter}/${x.name}`, x.deliveryState, x.internalId
        ])
        console.log(searcher)
    });
	let displayCrates = $derived(filterCrates(crates, filter));
</script>

<input bind:value={filter} type="text" class="input" placeholder="Suchen...">
<table class="table table-auto" style="width: 100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>OC</th>
        <th>Name</th>
        <th>Wo?</th>
    </tr>
    </thead>
    <tbody>
    {#each displayCrates as crate (crate.internalId)}
        <tr>
            <td>{crate.internalId}</td>
            <td>{crate.operationCenter}</td>
            <td>
                <a class="link" href="/crates/{crate.internalId}">{crate.name}</a>
            </td>
            <td>{crate.deliveryState}</td>
        </tr>
    {/each}
    </tbody>
</table>