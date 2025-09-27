<script lang="ts" module>
	export type InputType<T> = {
		label: string,
		keywords: string[],
		id: T
	}
</script>

<script lang="ts" generics="T">
	import {Query, SearcherFactory} from "@m31coding/fuzzy-search";

	type Props<T> = {
		inputs: InputType<T>[],
        selectedCallback: (t: T) => void
    }

	let {inputs, selectedCallback}: Props<T> = $props();

	const searcher = SearcherFactory.createDefaultSearcher<InputType<T>, T>();

	function filterCrates(oc: InputType<T>[], f: string): InputType<T>[] {
		if (f == "") return oc;
		return searcher.getMatches(new Query(f)).matches.toSorted((am, bm) => bm.quality - am.quality).map(it => it.entity);
	}

	let filter = $state("");
	$effect(() => {
		console.log("reindexing items")
		searcher.indexEntities(inputs, x => x.id, x => [
			x.label, ...x.keywords
		])
		console.log(searcher)
	});
	let displayCrates = $derived(filterCrates(inputs, filter));
</script>

<div class="flex flex-col gap-1">
    <input type="text" class="input w-full" placeholder="Search..." bind:value={filter}>
    <ul class="flex flex-col gap-1 overflow-auto max-h-100">
        {#each displayCrates as the (the.id)}
            <button class="btn" onclick={() => selectedCallback(the.id)}>{the.label}</button>
        {/each}
    </ul>
</div>