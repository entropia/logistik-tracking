<script lang="ts">
	import {onMount} from "svelte";
	import {currentUser} from "../stores/user";

	onMount(() => {
		if (!$currentUser) {
			currentUser.fetch()
        }
    })
</script>

<p>User: {$currentUser}</p>