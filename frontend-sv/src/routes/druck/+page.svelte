<script lang="ts">
	import { persisted } from "svelte-persisted-store";
	import type { ShoppingCart } from "$lib/printing_shopping_cart";
	import { execute, getCratesByIdMultiple, getListsByIdMultiple } from "$lib/graphql";
	import { untrack } from "svelte";

	import * as Table from "$lib/components/ui/table";

	import { client } from "$lib/http_api";
	import { Button } from "$lib/components/ui/button";
	import { Checkbox } from "$lib/components/ui/checkbox";
	import { Printer, Trash } from "@lucide/svelte";

	interface Printable {
		id: string;
		label: string;
	}

	let printStore = persisted<ShoppingCart>("printShoppingCart", {
		items: [],
	});

	let theCache: Record<string, Printable | null> = $state({});

	$effect(() => {
		let printStoreClone = $printStore.items;
		let idsToFetch = untrack(() => {
			let cachedItems = Object.keys(theCache);
			return printStoreClone.filter((it) => !cachedItems.includes(it));
		});
		if (idsToFetch.length != 0) {
			untrack(() => {
				for (let theid of idsToFetch) {
					theCache[theid] = null; // fetching it
				}
			});
			let cratesFetch = idsToFetch.filter((v) => v.startsWith("C"));
			let listsFetch = idsToFetch.filter((v) => v.startsWith("L"));
			if (cratesFetch.length > 0) {
				execute(getCratesByIdMultiple, window.fetch, {
					i: cratesFetch.map((it) => it.substring(1)),
				})
					.then((value) => {
						let intermediate: Record<string, Printable> = {};
						for (let multipleCratesByIdElement of value.data!!.getMultipleCratesById) {
							intermediate["C" + multipleCratesByIdElement.internalId] = {
								id: "C" + multipleCratesByIdElement.internalId,
								label: multipleCratesByIdElement.operationCenter + "/" + multipleCratesByIdElement.name,
							};
						}
						Object.assign(theCache, intermediate);
					})
					.catch(console.error);
			}
			if (listsFetch.length > 0) {
				execute(getListsByIdMultiple, window.fetch, {
					i: listsFetch.map((it) => it.substring(1)),
				})
					.then((value) => {
						let intermediate: Record<string, Printable> = {};
						for (let element of value.data!!.getMultipleListsById) {
							intermediate["L" + element.packingListId] = {
								id: "L" + element.packingListId,
								label: element.name,
							};
						}
						Object.assign(theCache, intermediate);
					})
					.catch(console.error);
			}
		}
	});

	let theCrates = $derived($printStore.items.map((it) => theCache[it]).filter((v) => !!v));
	// $inspect(theCrates)
	function printIt() {
		client
			.POST("/printMultiple", {
				body: theCrates.map((x) => {
					return {
						type: x.id.charAt(0) == "L" ? "List" : "Crate",
						id: parseInt(x.id.substring(1)),
					};
				}),
				parseAs: "stream",
			})
			.then((it) => it.response.blob())
			.then((it) => {
				let theOU = URL.createObjectURL(it);
				let opened = window.open(theOU, "_blank");
				if (!opened) {
					alert("Konnte kein Fenster öffnen! Bitte erlaube für diese Webseite popups.");
				}
				URL.revokeObjectURL(theOU);
			});
	}

	function empty() {
		printStore.update((it) => {
			it.items = [];
			return it;
		});
	}

	function toggle(internalId: string, it: ShoppingCart, targetState: boolean) {
		let el = internalId;
		let has = it.items.includes(el);
		if (!targetState && has) it.items = it.items.filter((f) => f != el);
		else if (targetState && !has) it.items.push(el);
		return it;
	}
</script>

<svelte:head>
	<title>Drucken</title>
</svelte:head>

<h2 class="text-2xl mb-2 font-bold">Drucken</h2>

<div class="flex flex-row gap-5 m-5 text-inherit">
	<Button disabled={theCrates.length === 0} onclick={printIt}>
		<Printer />
		Drucken
	</Button>
	<Button variant="destructive" disabled={theCrates.length === 0} onclick={empty}>
		<Trash />
		Leeren
	</Button>
</div>

<Table.Root class="w-full">
	<Table.Header>
		<Table.Row>
			<!--            w-0 für fit -->
			<Table.Head class="w-0">Druck?</Table.Head>
			<Table.Head>ID</Table.Head>
			<Table.Head>Name</Table.Head>
		</Table.Row>
	</Table.Header>
	<Table.Body>
		{#each theCrates as crate (crate.id)}
			<Table.Row>
				<Table.Cell>
					<Checkbox
						bind:checked={
							() => $printStore.items.includes(crate.id),
							(v) => {
								printStore.update((it) => toggle(crate.id, it, v));
							}
						}
					></Checkbox>
				</Table.Cell>
				<Table.Cell>{crate.id}</Table.Cell>
				<Table.Cell>
					{#if crate.id.startsWith("L")}
						<a class="link" href="/lists/{crate.id.substring(1)}">{crate.label}</a>
					{:else}
						<a class="link" href="/crates/{crate.id.substring(1)}">{crate.label}</a>
					{/if}
				</Table.Cell>
			</Table.Row>
		{/each}
	</Table.Body>
</Table.Root>
