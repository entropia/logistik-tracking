<script lang="ts">
	import type {PageProps} from './$types';
	import {addCratesToList, execute, removeCratesFromList, updateListPacking} from "$lib/graphql";
	import {prepare_id} from "$lib/id_parser";
	import {client} from "$lib/http_api";
	import {Button} from "$lib/components/ui/button";
	import * as Field from "$lib/components/ui/field";
	import * as Command from "$lib/components/ui/command";
	import * as Popover from "$lib/components/ui/popover";
	import * as Table from "$lib/components/ui/table";
	import {toast} from "svelte-sonner";
	import {Input} from "$lib/components/ui/input";
	import {tick} from "svelte";
	import {ChevronsUpDown, Plus as PlusIcon} from "@lucide/svelte";
	import DeliveryStateDropdown from "$lib/components/ours/DeliveryStateDropdown.svelte";

	let {data}: PageProps = $props();

	let current = $state(data.list)

	let form_deliverystate = $state(current.deliveryStatet);

	async function handle_submit(event: SubmitEvent) {
		event.preventDefault()
		let resp = await execute(updateListPacking, fetch, {
			id: current.packingListId,
			newstate: form_deliverystate
		})
		let updated = resp.data?.setPackingListDeliveryState
		if (updated) {
			// display new info
			current.deliveryStatet = updated.deliveryStatet
			current.packedCrates = updated.packedCrates
		}
		toast.success("Liste gespeichert")
	}

	async function run_del(ev: MouseEvent, id: string) {
		(ev.currentTarget as Element).setAttribute("disabled", "true")
		let resp = await execute(removeCratesFromList, fetch, {
			pl: current.packingListId,
			crates: [id]
		})
		let updated = resp.data?.removeCratesFromPackingList
		if (updated) {
			// display new info
			current.packedCrates = updated.packedCrates
		} else {
			// something went wrong
			(ev.currentTarget as Element).removeAttribute("disabled")
		}
		toast.success("Kiste entfernt")
	}

	async function add(id: string) {
		id = prepare_id(id);
		let resp = await execute(addCratesToList, fetch, {
			pl: current.packingListId,
			crates: [id]
		})
		let updated = resp.data?.addCratesToPackingList
		if (updated) {
			// display new info
			current.packedCrates = updated.packedCrates
		}
		toast.success("Kiste hinzugefügt")
	}

	let crate_id = $state<string>();

	async function add_crate(ev: SubmitEvent) {
		// jens war hier :)
		ev.preventDefault()
		await add(crate_id!!);
		(ev.target as HTMLFormElement).reset()
	}


	let id_input = $state<HTMLInputElement | null>(null);

	function printThisList() {
		client.POST("/printMultiple", {
			body: [
				{id: parseInt(current.packingListId), type: "List"},
				...current.packedCrates.map<{ type: "Crate", id: number }>(it => {
					return {type: "Crate", id: parseInt(it.internalId)}
				})
			],
			parseAs: "stream"
		})
			.then(it => it.response.blob())
			.then(it => {
				let theOU = URL.createObjectURL(it)
				let opened = window.open(theOU, "_blank")
				if (!opened) {
					alert("Konnte kein Fenster öffnen! Bitte erlaube für diese Webseite popups.")
				}
				URL.revokeObjectURL(theOU)
			})
	}

	let open = $state(false);
	let triggerRef = $state<HTMLButtonElement>(null!);

	// We want to refocus the trigger button when the user selects
	// an item from the list so users can continue navigating the
	// rest of the form with the keyboard.
	function closeAndFocusTrigger() {
		open = false;
		tick().then(() => {
			triggerRef.focus();
		});
	}
</script>

<svelte:document onkeydown={(ev: KeyboardEvent) => {
    if (ev.key === "C" || ev.key === "c") id_input?.focus()
}}></svelte:document>

<h2 class="text-2xl mb-5 font-bold">Liste {current.name}</h2>
<Button onclick={printThisList} class="mb-5">
	Liste Drucken
</Button>

<form onsubmit={handle_submit} class="w-full max-w-md mb-5">
	<Field.Set>
		<Field.Field>
			<Field.Label for="status">Status</Field.Label>
            <DeliveryStateDropdown id="status" bind:value={form_deliverystate}></DeliveryStateDropdown>
		</Field.Field>
		<Field.Field>
			<Button type="submit">Speichern</Button>
		</Field.Field>
	</Field.Set>
</form>

<h3 class="text-xl mb-2 mt-2">Kisten:</h3>

<form onsubmit={add_crate} class="flex flex-row flex-wrap gap-4 items-baseline w-full max-w-xl">
	<Field.Field orientation="horizontal" class="flex-1 min-w-[200px]">
		<Input type="text" class="input" placeholder="Kisten-ID" required pattern="[cC]?\d+" bind:value={crate_id}
			   bind:ref={id_input}></Input>
		<Button type="submit">
			<PlusIcon/>
		</Button>
	</Field.Field>
	<span class="flex-0">oder</span>
	<div class="flex-0">
		<Popover.Root bind:open>
			<Popover.Trigger bind:ref={triggerRef}>
				{#snippet child({props})}
					<Button
						{...props}
						variant="outline"
						class="w-[200px] justify-between"
						role="combobox"
						aria-expanded={open}
					>
						Kiste Suchen
						<ChevronsUpDown class="opacity-50"/>
					</Button>
				{/snippet}
			</Popover.Trigger>
			<Popover.Content class="w-[200px] p-0">
				<Command.Root>
					<Command.Input placeholder="Kiste suchen..."/>
					<Command.List>
						<Command.Empty>¯\_(ツ)_/¯</Command.Empty>
						<Command.Group value="crates">
							{#each data.crates as crate (crate.internalId)}
								<Command.Item
									value="{crate.operationCenter}/{crate.name}"
									onSelect={() => {
										closeAndFocusTrigger();
										add(crate.internalId)
                                    }}
									disabled={current.packedCrates.some(it => it.internalId === crate.internalId)}
								>
									{crate.operationCenter} / {crate.name}
								</Command.Item>
							{/each}
						</Command.Group>
					</Command.List>
				</Command.Root>
			</Popover.Content>
		</Popover.Root>
	</div>
</form>

<Table.Root class="w-full mt-5">
	<Table.Header>
		<Table.Row>
			<!--            w-0 für fit -->
			<Table.Head>ID</Table.Head>
			<Table.Head>OC</Table.Head>
			<Table.Head>Name</Table.Head>
			<Table.Head>Wo?</Table.Head>
			<Table.Head class="w-0"></Table.Head>
		</Table.Row>
	</Table.Header>
	<Table.Body>
		{#each current.packedCrates as crate (crate.internalId)}
			<Table.Row>
				<Table.Cell>{crate.internalId}</Table.Cell>
				<Table.Cell>{crate.operationCenter}</Table.Cell>
				<Table.Cell>
					<a class="link" href="/crates/{crate.internalId}">{crate.name}</a>
				</Table.Cell>
				<Table.Cell>{crate.deliveryState}</Table.Cell>
				<Table.Cell>
					<Button size="sm" variant="destructive" onclick={(ev) => run_del(ev, crate.internalId)}>
						Entf.
					</Button>
				</Table.Cell>
			</Table.Row>
		{/each}
	</Table.Body>
</Table.Root>