<script lang="ts">
	// import favicon from '$lib/assets/favicon.svg';

    import "../app.css";
	import UserMenu from "../components/UserMenu.svelte";
	import {Button} from "$lib/components/ui/button";

	let { children } = $props();
</script>

<div class="flex flex-row p-2 border-b-primary border-l-transparent border-r-transparent border-t-transparent border-2">
    <div class="flex-1">
        <Button variant="ghost" href="/" class="text-xl">Logitrack</Button>
    </div>
    <div class="flex-none">
        <ul class="flex flex-row gap-2">

            <li><Button variant="ghost" href="/druck">Drucken</Button></li>
            <li><Button variant="ghost" href="/crates">Kisten</Button></li>
            <li><Button variant="ghost" href="/lists">Listen</Button></li>
<!--            todo: user menu dropdown -->
<!--            <li>-->
<!--                <Dropdown bind:showTooltip={showTooltip}>-->
<!--                    {#snippet trigger(flRef)}-->
<!--                        <button use:flRef aria-label="account" onclick={() => showTooltip = true}>-->
<!--                            <svg style="width: 21px; height: 21px" class="icon-[material-symbols&#45;&#45;account-circle]"></svg>-->
<!--                        </button>-->
<!--                    {/snippet}-->
<!--                    {#snippet body()}-->
<!--                        <ul class="dropdown menu w-52 rounded-box bg-base-100 shadow-sm">-->
<!--                            <UserMenu></UserMenu>-->
<!--                        </ul>-->
<!--                    {/snippet}-->
<!--                </Dropdown>-->
<!--            </li>-->
<!--            <li>-->
<!--                <details>-->
<!--                    <summary>Parent</summary>-->
<!--                    <ul class="bg-base-100 rounded-t-none p-2">-->
<!--                        <li><a href="/">Link 1</a></li>-->
<!--                        <li><a href="/">Link 2</a></li>-->
<!--                    </ul>-->
<!--                </details>-->
<!--            </li>-->
        </ul>
    </div>
</div>

<!--<svelte:head>-->
<!--	<link rel="icon" href={favicon} />-->
<!--</svelte:head>-->
<div class="m-4">
    {@render children?.()}
</div>
