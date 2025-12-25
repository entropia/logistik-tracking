<script lang="ts">
	// import favicon from '$lib/assets/favicon.svg';

    import "../app.css";
	import {Button} from "$lib/components/ui/button";
	import {Toaster} from "$lib/components/ui/sonner";
	import {ModeWatcher} from "mode-watcher";
	import UserDropdown from "$lib/components/ours/UserDropdown.svelte";

	let { children } = $props();
</script>

<ModeWatcher />
<Toaster position="top-center" />

<div class="flex flex-row p-2 border-b-primary border-l-transparent border-r-transparent border-t-transparent border-2">
    <div class="flex-1">
        <Button variant="ghost" href="/" class="text-xl">Logitrack</Button>
    </div>
    <div class="flex-none">
        <ul class="flex flex-row gap-2">

            <li><Button variant="ghost" href="/druck">Drucken</Button></li>
            <li><Button variant="ghost" href="/crates">Kisten</Button></li>
            <li><Button variant="ghost" href="/lists">Listen</Button></li>
            <li><UserDropdown></UserDropdown></li>
        </ul>
    </div>
</div>

<!--<svelte:head>-->
<!--	<link rel="icon" href={favicon} />-->
<!--</svelte:head>-->
<div class="m-4">
    {@render children?.()}
</div>
