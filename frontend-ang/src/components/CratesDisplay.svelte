<script lang="ts">
	import {DeliveryState, OperationCenter} from "../gen/graphql";
	import {Query, SearcherFactory} from '@m31coding/fuzzy-search';
	import {persisted} from "svelte-persisted-store";
	import type {ShoppingCart} from "$lib/printing_shopping_cart";

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
		console.log("reindex", crates)
		searcher.indexEntities(crates, x => x.internalId, x => [
			x.operationCenter, x.name, `${x.operationCenter}/${x.name}`, x.deliveryState, x.internalId
        ])
    });
	let displayCrates = $derived(filterCrates(crates, filter));

	let printStore = persisted<ShoppingCart>('printShoppingCart', {
		items: []
    })

	function toggle(internalId: string, it: ShoppingCart, targetState: boolean) {
		let el = "C"+internalId;
		let has = it.items.includes(el);
		if (!targetState && has) it.items = it.items.filter(f => f != el)
        else if (targetState && !has) it.items.push(el)
        return it;
	}
</script>

<input bind:value={filter} type="text" class="input" placeholder="Suchen...">
<table class="table table-auto" style="width: 100%">
    <thead>
    <tr>
        <th>Druck?</th>
        <th>ID</th>
        <th>OC</th>
        <th>Name</th>
        <th>Wo?</th>
    </tr>
    </thead>
    <tbody>
    {#each displayCrates as crate (crate.internalId)}
        <tr>
            <td>
                <input type="checkbox" class="checkbox" bind:checked={
                () => $printStore.items.includes("C"+crate.internalId),
                (v) => {printStore.update(it => toggle(crate.internalId, it, v))}
                }>
            </td>
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