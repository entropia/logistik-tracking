<script lang="ts">
	// import favicon from '$lib/assets/favicon.svg';

    import "../app.css";
	import UserMenu from "../components/UserMenu.svelte";

	let { children } = $props();

	import Dropdown from "../components/Dropdown.svelte";

	let showTooltip = $state(false);
</script>

<div class="navbar bg-base-100 shadow-lg mb-2">
    <div class="flex-1">
        <a class="btn btn-ghost text-xl" href="/">Logitrack</a>
    </div>
    <div class="flex-none">
        <ul class="menu menu-horizontal px-1">

            <li><a href="/druck">Drucken</a></li>
            <li><a href="/crates">Kisten</a></li>
            <li><a href="/lists">Listen</a></li>
            <li>
                <Dropdown bind:showTooltip={showTooltip}>
                    {#snippet trigger(flRef)}
                        <button use:flRef aria-label="account" onclick={() => showTooltip = true}>
                            <svg style="width: 21px; height: 21px" class="icon-[material-symbols--account-circle]"></svg>
                        </button>
                    {/snippet}
                    {#snippet body()}
                        <ul class="dropdown menu w-52 rounded-box bg-base-100 shadow-sm">
                            <UserMenu></UserMenu>
                        </ul>
                    {/snippet}
                </Dropdown>
            </li>
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
<div class="m-2">
    {@render children?.()}
</div>
