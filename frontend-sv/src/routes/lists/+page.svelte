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
    import { Plus, SearchIcon, SquareStack } from '@lucide/svelte';
    import * as InputGroup from '$lib/components/ui/input-group';

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

	function setState(internalId: string, it: ShoppingCart, targetState: boolean | null) {
        let el = "L" + internalId;
        let has = it.items.includes(el);
        if (targetState == null) {
            // plain toggle
            if (has) it.items = it.items.filter((f) => f != el);
            else it.items.push(el);
        } else {
            // set
            if (!targetState && has) it.items = it.items.filter((f) => f != el);
            else if (targetState && !has) it.items.push(el);
        }
        return it;
    }

    function getPrintStateCallback(id: string) {
        return () => $printStore.items.includes("L" + id);
    }

    function getPrintStateSetCallback(id: string) {
        return (v: boolean) => printStore.update((it) => setState(id, it, v));
    }

	let filter = $state("");
	$effect(() => {
		console.log("reindexing lists")
		searcher.indexEntities(data.lists, x => x.packingListId, x => [
			x.name, x.deliveryStatet, x.packingListId
		])
		console.log(searcher)
	});
	let displayLists = $derived(filterCrates(data.lists, filter));
</script>

<svelte:head>
    <title>Listen</title>
</svelte:head>

<h2 class="text-2xl mb-2 font-bold">Listen</h2>
<div class="flex flex-row gap-5 m-5">
    <Button href="/lists/create">
		<Plus />
        Erstellen
    </Button>
    <Button href="/lists/masseneintragung" variant="secondary">
		<SquareStack />
        Masseneintragung
    </Button>
</div>

<InputGroup.Root class="w-full max-w-[400px]">
    <InputGroup.Input bind:value={filter} type="text" placeholder="Suchen..." />
    <InputGroup.Addon>
        <SearchIcon />
    </InputGroup.Addon>
</InputGroup.Root>
<Table.Root class="w-full mt-2">
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
            <Table.Row onclick={() => printStore.update((it) => setState(crate.packingListId, it, null))}>
                <Table.Cell>
                    <!-- see below -->
                    <Checkbox onclick={(v) => v.stopPropagation()} bind:checked={getPrintStateCallback(crate.packingListId), getPrintStateSetCallback(crate.packingListId)}></Checkbox>
                </Table.Cell>
                <Table.Cell>{crate.packingListId}</Table.Cell>
                <Table.Cell>
                    <a class="link" href="/lists/{crate.packingListId}" onclick={(v) => v.stopPropagation()}>{crate.name}</a>
                </Table.Cell>
                <Table.Cell>{crate.deliveryStatet}</Table.Cell>
            </Table.Row>
        {/each}
    </Table.Body>
</Table.Root>