<script lang="ts">
	import {persisted} from "svelte-persisted-store";
	import type {ShoppingCart} from "$lib/printing_shopping_cart";
	import type {GetMoreCratesQuery} from "../../gen/graphql";
	import {execute, getCratesByIdMultiple} from "$lib/graphql";
	import CratesDisplay from "../../components/CratesDisplay.svelte";
	import {untrack} from "svelte";

	let printStore = persisted<ShoppingCart>('printShoppingCart', {
		items: []
	})

    let theCache: Record<string, GetMoreCratesQuery["getMultipleCratesById"][0] | null> = $state({});

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
			execute(getCratesByIdMultiple, window.fetch, {
				i: idsToFetch.map(it => it.substring(1))
			}).then(value => {
                let intermediate = {}
				for (let multipleCratesByIdElement of value.data!!.getMultipleCratesById) {
					intermediate["C"+multipleCratesByIdElement.internalId] = multipleCratesByIdElement
				}
				Object.assign(theCache, intermediate)
			}).catch(console.error)
        }
    })

	import { PUBLIC_API_URL } from '$env/static/public';

    let theCrates = $derived($printStore.items.map(it => theCache[it]).filter(v => !!v))
    // $inspect(theCrates)
	function printIt() {
        fetch(PUBLIC_API_URL+"/api/printMultiple", {
			method: "POST",
            credentials: "include",
            body: JSON.stringify(theCrates.map(x => {return {type: "Crate", id: x.internalId}})),
            headers: {
				"Content-Type": "application/json"
            }
        }).then(it => it.blob())
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
</script>

<button class="btn btn-success" disabled={theCrates.length === 0} onclick={printIt}>Drucken</button>
<button class="btn btn-error" disabled={theCrates.length === 0} onclick={empty}>Leeren</button>
<CratesDisplay crates={theCrates}></CratesDisplay>

<!--{#each theCrates as bruh (bruh.internalId)}-->
<!--<p>{bruh.name}</p>-->
<!--{/each}-->

