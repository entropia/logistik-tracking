<script lang="ts">
	import {persisted} from "svelte-persisted-store";
	import type {ShoppingCart} from "$lib/printing_shopping_cart";
	import {execute, getCratesByIdMultiple, getListsByIdMultiple} from "$lib/graphql";
	import {untrack} from "svelte";

	interface Printable {
		id: string;
		label: string;
    }

	let printStore = persisted<ShoppingCart>('printShoppingCart', {
		items: []
	})

    let theCache: Record<string, Printable | null> = $state({});

    $effect(() => {
		$inspect.trace("whatthefuck");
		let bruih = $printStore.items;
		console.log("AT EFFECT",bruih);
		let idsToFetch = untrack(() => {
			let cachedItems = Object.keys(theCache);
			return bruih.filter(it => !cachedItems.includes(it))
        })
		if (idsToFetch.length != 0) {
			console.log("fetching", idsToFetch);
			untrack(() => {
				for (let theid of idsToFetch) {
					theCache[theid] = null; // fetching it
				}
            })
            let cratesFetch = idsToFetch.filter((v) => v.startsWith("C"))
            let listsFetch = idsToFetch.filter((v) => v.startsWith("L"))
			if (cratesFetch.length > 0) {
				execute(getCratesByIdMultiple, window.fetch, {
					i: cratesFetch.map(it => it.substring(1))
				}).then(value => {
					let intermediate: Record<string, Printable> = {}
					for (let multipleCratesByIdElement of value.data!!.getMultipleCratesById) {
						intermediate["C"+multipleCratesByIdElement.internalId] = {
							id: "C"+multipleCratesByIdElement.internalId,
                            label: multipleCratesByIdElement.operationCenter+"/"+multipleCratesByIdElement.name
                        }
					}
					Object.assign(theCache, intermediate)
				}).catch(console.error)
            }
			if (listsFetch.length > 0) {
				execute(getListsByIdMultiple, window.fetch, {
					i: listsFetch.map(it => it.substring(1))
				}).then(value => {
					let intermediate: Record<string, Printable> = {}
					for (let element of value.data!!.getMultipleListsById) {
						intermediate["L"+element.packingListId] = {
							id: "L"+element.packingListId,
                            label: element.name
                        }
					}
					Object.assign(theCache, intermediate)
				}).catch(console.error)
            }
        }
    })

	import {client} from "$lib/http_api";

    let theCrates = $derived($printStore.items.map(it => theCache[it]).filter(v => !!v))
    // $inspect(theCrates)
	function printIt() {

		client.POST("/printMultiple", {
			body: theCrates.map(x => {return {type: x.id.charAt(0) == 'L' ? "List" : "Crate", id: parseInt(x.id.substring(1))}}),
            parseAs: "stream"
		})
            .then(it => it.response.blob())
            .then(it => {
                let theOU = URL.createObjectURL(it)
                let opened = window.open(theOU, "_blank")
                if (!opened) {
					alert("Konnte kein Fenster öffnen! Bitte erlaube für diese Webseite popups.")
                }
				URL.revokeObjectURL(theOU)
            })
	}

	function empty() {
		printStore.update(it => {
			it.items = [];
			return it;
        })
    }

	function toggle(internalId: string, it: ShoppingCart, targetState: boolean) {
		let el = "C"+internalId;
		let has = it.items.includes(el);
		if (!targetState && has) it.items = it.items.filter(f => f != el)
		else if (targetState && !has) it.items.push(el)
		return it;
	}
</script>

<button class="btn btn-success" disabled={theCrates.length === 0} onclick={printIt}>Drucken</button>
<button class="btn btn-error" disabled={theCrates.length === 0} onclick={empty}>Leeren</button>

<!--todo: fix for shadcn-->
<table class="table table-auto" style="width: 100%">
    <thead>
    <tr>
        <th>Druck?</th>
        <th>ID</th>
        <th>Name</th>
    </tr>
    </thead>
    <tbody>
    {#each theCrates as crate (crate.id)}
        <tr>
            <td>
                <input type="checkbox" class="checkbox" bind:checked={
                () => $printStore.items.includes(crate.id),
                (v) => {printStore.update(it => toggle(crate.id, it, v))}
                }>
            </td>
            <td>{crate.id}</td>
            <td>
                {#if crate.id.startsWith("L")}
                    <a class="link" href="/lists/{crate.id.substring(1)}">{crate.label}</a>
                {:else}
                    <a class="link" href="/crates/{crate.id.substring(1)}">{crate.label}</a>
                {/if}
            </td>
        </tr>
    {/each}
    </tbody>
</table>