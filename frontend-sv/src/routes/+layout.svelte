<script lang="ts">
    // import favicon from '$lib/assets/favicon.svg';

    import "../app.css";
    import { Button } from "$lib/components/ui/button";
    import { Toaster } from "$lib/components/ui/sonner";
    import { ModeWatcher } from "mode-watcher";
    import UserDropdown from "$lib/components/ours/UserDropdown.svelte";
    import * as Dropdown from "$lib/components/ui/dropdown-menu";
    import { Menu } from "@lucide/svelte";
    import { goto } from "$app/navigation";

    let { children } = $props();
</script>

<ModeWatcher />
<Toaster position="top-center" />

<div class="flex flex-row p-2 border-b-primary border-l-transparent border-r-transparent border-t-transparent border-2">
    <div class="flex-1">
        <Button variant="ghost" href="/" class="text-xl">Logitrack</Button>
    </div>
    <div class="flex-none">
        <ul class="hidden sm:flex flex-row gap-2">
            <li><Button variant="ghost" href="/druck">Drucken</Button></li>
            <li><Button variant="ghost" href="/crates">Kisten</Button></li>
            <li><Button variant="ghost" href="/lists">Listen</Button></li>
            <li><Button variant="ghost" href="/scan">Scan</Button></li>
            <li>
                <Dropdown.Root>
                    <Dropdown.Trigger>
                        {#snippet child({ props })}
                            <Button {...props} variant="ghost">Benutzer</Button>
                        {/snippet}
                    </Dropdown.Trigger>
                    <Dropdown.Content>
                        <UserDropdown></UserDropdown>
                    </Dropdown.Content>
                </Dropdown.Root>
            </li>
        </ul>
        <div class="sm:hidden">
            <Dropdown.Root>
                <Dropdown.Trigger class="h-inherit">
                    {#snippet child({ props })}
                        <Button {...props} size="icon" variant="outline"><Menu></Menu></Button>
                    {/snippet}
                </Dropdown.Trigger>
                <Dropdown.Content>
                    <Dropdown.Group>
                        <Dropdown.Label>Menü</Dropdown.Label>
                        <Dropdown.Separator />
                        <Dropdown.Item onSelect={() => goto("/druck")}>Drucken</Dropdown.Item>
                        <Dropdown.Item onSelect={() => goto("/crates")}>Kisten</Dropdown.Item>
                        <Dropdown.Item onSelect={() => goto("/lists")}>Listen</Dropdown.Item>
                        <Dropdown.Item onSelect={() => goto("/scan")}>Scan</Dropdown.Item>
                        <Dropdown.Separator></Dropdown.Separator>
                        <UserDropdown></UserDropdown>
                    </Dropdown.Group>
                </Dropdown.Content>
            </Dropdown.Root>
        </div>
    </div>
</div>

<!--<svelte:head>-->
<!--	<link rel="icon" href={favicon} />-->
<!--</svelte:head>-->
<div class="m-4">
    {@render children?.()}
</div>
