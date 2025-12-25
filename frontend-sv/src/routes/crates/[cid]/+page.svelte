<script lang="ts">
    import type { PageProps } from "./$types";
    import CrateEditForm from "$lib/components/ours/CrateEditForm.svelte";

    let { data }: PageProps = $props();
</script>

<svelte:head>
    <title>Kiste {data.crate.operationCenter} / {data.crate.name}</title>
</svelte:head>

<h2 class="text-2xl mb-5 font-bold">
    Box {data.crate.operationCenter} / {data.crate.name}
</h2>

<div class="w-full max-w-3xl mb-5">
    <CrateEditForm data={data.form}></CrateEditForm>
</div>

{#if data.crate.packingList}
    <a class="text-xl mt-4 mb-2 link font-medium" href="/lists/{data.crate.packingList.packingListId}">
        Zugehörige Liste {data.crate.packingList.packingListId}
    </a>
    <p>Name: {data.crate.packingList.name}</p>
    <p>Status: {data.crate.packingList.deliveryStatet}</p>
{/if}
