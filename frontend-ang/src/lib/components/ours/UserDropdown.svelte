<script lang="ts">
    import * as Dropdown from "$lib/components/ui/dropdown-menu";
	import {Button} from "$lib/components/ui/button";
	import {LogOut, User, UserCog} from "@lucide/svelte";

	import {onMount} from "svelte";
	import {currentUser} from "$lib/stores/user";
	import {goto} from "$app/navigation";

	import { PUBLIC_API_URL } from '$env/static/public';

	onMount(() => {
		if (!$currentUser) {
			currentUser.fetch()
		}
	})

    async function checkNeedsLogin() {
		if ($currentUser == null) {
			await goto("/users/login")
        }
    }

	async function logout() {
		window.location.href = PUBLIC_API_URL+"/api/logout";
    }

	async function sendToUM() {
		await goto("/users")
    }
</script>

<Dropdown.Root>
    <Dropdown.Trigger>
        {#snippet child({ props })}
            <Button {...props} variant="ghost">Benutzer</Button>
        {/snippet}
    </Dropdown.Trigger>
    <Dropdown.Content>
        <Dropdown.Item onclick={checkNeedsLogin}>
            <User />
            {$currentUser == null ? "Anmelden" : `Angemeldet als: ${$currentUser.username}`}
        </Dropdown.Item>
        {#if ($currentUser != null && $currentUser.authorities.includes("MANAGE_USERS"))}
            <Dropdown.Item onclick={sendToUM}>
                <UserCog></UserCog>
                Nutzer Verwalten
            </Dropdown.Item>
        {/if}
        {#if ($currentUser != null)}
            <Dropdown.Item variant="destructive" onclick={logout}>
                <LogOut />
                Abmelden
            </Dropdown.Item>
        {/if}
    </Dropdown.Content>
</Dropdown.Root>