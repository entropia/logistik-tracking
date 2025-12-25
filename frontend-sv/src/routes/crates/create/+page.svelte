<script lang="ts">
	import type { PageProps } from "./$types";
    import CrateCreateForm from "$lib/components/ours/CrateCreateForm.svelte";

    let { data }: PageProps = $props();
</script>

<svelte:head>
    <title>Kiste erstellen</title>
</svelte:head>

<h2 class="text-2xl mb-5 font-bold">Box erstellen</h2>


<div class="w-full max-w-3xl mb-5">
    <CrateCreateForm data={data.form}></CrateCreateForm>
</div>