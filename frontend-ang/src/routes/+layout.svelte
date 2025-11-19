<script lang="ts">
	// import favicon from '$lib/assets/favicon.svg';

    import "../app.css";

	let { children } = $props();
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
