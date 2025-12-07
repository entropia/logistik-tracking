<script lang="ts">
    import { DeliveryState, OperationCenter } from "$lib/../gen/graphql";
    import { Query, SearcherFactory } from "@m31coding/fuzzy-search";
    import { persisted } from "svelte-persisted-store";
    import type { ShoppingCart } from "$lib/printing_shopping_cart";
    import * as Table from "$lib/components/ui/table";
    import { Checkbox } from "$lib/components/ui/checkbox/index";
    import * as InputGroup from "$lib/components/ui/input-group";
    import { SearchIcon } from "@lucide/svelte";

    type CrateLike = {
        deliveryState: DeliveryState;
        internalId: string;
        name: string;
        operationCenter: OperationCenter;
    };

    const searcher = SearcherFactory.createDefaultSearcher<CrateLike, string>();

    function filterCrates(oc: CrateLike[], f: string): CrateLike[] {
        if (f == "") return oc;
        return searcher
            .getMatches(new Query(f))
            .matches.toSorted((am, bm) => bm.quality - am.quality)
            .map((it) => it.entity);
    }

    let { crates }: { crates: CrateLike[] } = $props();
    let filter = $state("");
    $effect(() => {
        console.log("reindex", crates);
        searcher.indexEntities(
            crates,
            (x) => x.internalId,
            (x) => [
                x.operationCenter,
                x.name,
                `${x.operationCenter}/${x.name}`,
                x.deliveryState,
                x.internalId,
            ],
        );
    });
    let displayCrates = $derived(filterCrates(crates, filter));

    let printStore = persisted<ShoppingCart>("printShoppingCart", {
        items: [],
    });

    function toggle(
        internalId: string,
        it: ShoppingCart,
        targetState: boolean,
    ) {
        let el = "C" + internalId;
        let has = it.items.includes(el);
        if (!targetState && has) it.items = it.items.filter((f) => f != el);
        else if (targetState && !has) it.items.push(el);
        return it;
    }
</script>

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
            <Table.Head>OC</Table.Head>
            <Table.Head>Name</Table.Head>
            <Table.Head>Wo?</Table.Head>
        </Table.Row>
    </Table.Header>
    <Table.Body>
        {#each displayCrates as crate (crate.internalId)}
            <Table.Row>
                <Table.Cell>
                    <Checkbox
                        bind:checked={
                            () =>
                                $printStore.items.includes(
                                    "C" + crate.internalId,
                                ),
                            (v) => {
                                printStore.update((it) =>
                                    toggle(crate.internalId, it, v),
                                );
                            }
                        }
                    ></Checkbox>
                </Table.Cell>
                <Table.Cell>{crate.internalId}</Table.Cell>
                <Table.Cell>{crate.operationCenter}</Table.Cell>
                <Table.Cell>
                    <a class="link" href="/crates/{crate.internalId}"
                        >{crate.name}</a
                    >
                </Table.Cell>
                <Table.Cell>{crate.deliveryState}</Table.Cell>
            </Table.Row>
        {/each}
    </Table.Body>
</Table.Root>
