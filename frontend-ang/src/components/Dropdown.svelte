<script lang="ts">
	import { offset, flip, shift } from "svelte-floating-ui/dom";
	import type {ComputeConfig, ContentAction, ReferenceAction} from "svelte-floating-ui";
	import {createFloatingActions} from "svelte-floating-ui";
	import type {Snippet} from "svelte";

	let {trigger, body, settingsOverride, showTooltip = $bindable()}: {
		trigger: Snippet<[ReferenceAction]>,
        body: Snippet<[]>,
		settingsOverride?: ComputeConfig,
        showTooltip: boolean
    } = $props();

	const [ floatingRef, floatingContent ] = createFloatingActions({
		strategy: "absolute",
		placement: "top",
		middleware: [
			offset(6),
			flip(),
			shift(),
		],
        ...settingsOverride
	});

	let theDiv = $state<HTMLDivElement | undefined>(undefined);

	function handleClick(ev: MouseEvent & {
		currentTarget: EventTarget & Window;
	}) {
		if (ev.defaultPrevented) return;

        // console.log(showTooltip, theDiv, theTrigger)
        if (showTooltip && theDiv) {
			console.log("clicked", ev.target, "wanted", theDiv)
			if (!theDiv.contains(ev.target as Node)) {
				// clicked outside the div
                showTooltip = false;
            }
        }
    }
	function handleKeyDown(e: KeyboardEvent) {
        if (e.defaultPrevented) return;
		if (e.key == "Escape") {
			showTooltip = false;
        }
    }
</script>



<svelte:window onmousedown={handleClick} onkeydown={handleKeyDown}></svelte:window>

{@render trigger(floatingRef)}

{#if showTooltip}
    <div bind:this={theDiv} class="z-100" use:floatingContent>
        {@render body()}
    </div>
{/if}