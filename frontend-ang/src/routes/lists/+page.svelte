<script lang="ts">
	import type { PageProps } from './$types';
	import {Query, SearcherFactory} from "@m31coding/fuzzy-search";
	import type {DeliveryState} from "../../gen/graphql";
	import type {ShoppingCart} from "$lib/printing_shopping_cart";
	import {persisted} from "svelte-persisted-store";
	import {Button} from "$lib/components/ui/button";
	import {Input} from "$lib/components/ui/input";
	import * as Table from "$lib/components/ui/table";
	import {Checkbox} from "$lib/components/ui/checkbox";

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

	let printStore = persisted<ShoppingCart>('printShoppingCart', {
		items: []
	})

	function toggle(internalId: string, it: ShoppingCart, targetState: boolean) {
		let el = "L"+internalId;
		let has = it.items.includes(el);
		if (!targetState && has) it.items = it.items.filter(f => f != el)
		else if (targetState && !has) it.items.push(el)
		return it;
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
    <Button href="/lists/create">
        Erstellen
    </Button>
    <Button href="/lists/masseneintragung" variant="secondary">
        Masseneintragung
    </Button>
</div>

<Input bind:value={filter} type="text" class="input w-full max-w-200" placeholder="Suchen..." ></Input>
<Table.Root class="w-full mt-5">
    <Table.Header>
        <Table.Row>
            <!--            w-0 für fit -->
            <Table.Head class="w-0">Druck?</Table.Head>
            <Table.Head>ID</Table.Head>
            <Table.Head>Name</Table.Head>
            <Table.Head>Wo?</Table.Head>
        </Table.Row>
    </Table.Header>
    <Table.Body>
        {#each displayLists as crate (crate.packingListId)}
            <Table.Row>
                <Table.Cell>
                    <Checkbox bind:checked={
                () => $printStore.items.includes("C"+crate.packingListId),
                (v) => {printStore.update(it => toggle(crate.packingListId, it, v))}
                }>

                    </Checkbox>
                </Table.Cell>
                <Table.Cell>{crate.packingListId}</Table.Cell>
                <Table.Cell>
                    <a class="link" href="/lists/{crate.packingListId}">{crate.name}</a>
                </Table.Cell>
                <Table.Cell>{crate.deliveryStatet}</Table.Cell>
            </Table.Row>
        {/each}
    </Table.Body>
</Table.Root>