<script lang="ts">
	import type {PageProps} from './$types';
	import * as Table from "$lib/components/ui/table"
	import {Checkbox} from "$lib/components/ui/checkbox";
    import { Button } from '$lib/components/ui/button';
    import { Plus } from '@lucide/svelte';

	let {data}: PageProps = $props();
	console.log(data)
</script>

<svelte:head>
    <title>Nutzer</title>
</svelte:head>

<h2 class="text-2xl mb-2 font-bold">Nutzerverwaltung</h2>

<div class="flex flex-row gap-5 m-5">
    <Button href="/users/create">
		<Plus />
        Erstellen
    </Button>
</div>

<Table.Root class="w-full mt-2">
    <Table.Header>
        <Table.Row>
            <Table.Head class="w-0">Aktiv?</Table.Head>
            <Table.Head>Name</Table.Head>
            <Table.Head>Rechte</Table.Head>
        </Table.Row>
    </Table.Header>
    <Table.Body>
        {#each data.users as user (user.username)}
            <Table.Row>
                <Table.Cell>
                    <Checkbox checked={user.enabled} readonly></Checkbox>
                </Table.Cell>
                <Table.Cell>
                    <a class="link" href={"/users/manage/"+user.username}>{user.username}</a>
                </Table.Cell>
                <Table.Cell>
                    {user.authorities.join(", ")}
                </Table.Cell>
            </Table.Row>
        {/each}
    </Table.Body>
</Table.Root>