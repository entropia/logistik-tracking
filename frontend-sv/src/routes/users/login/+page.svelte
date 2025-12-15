<script lang="ts">
    import { PUBLIC_API_URL } from "$env/static/public";
    import { page } from "$app/state";
    import { Info as InfoIcon, CircleAlert, User, KeyRound } from "@lucide/svelte";
    import * as Alert from "$lib/components/ui/alert";
    import * as Field from "$lib/components/ui/field";
    import { Button } from "$lib/components/ui/button";
    import * as InputGroup from "$lib/components/ui/input-group";

    let message = page.url.searchParams.get("message");
    let redir = page.url.searchParams.get("redirect");
    let redirUrl: URL | null = null;
    if (redir != null) redirUrl = new URL(redir, page.url.origin);
    let redirUrlWarning: URL | null = null;
    if (redirUrl != null && redirUrl.origin != page.url.origin) {
        // jemand macht hier blödsinn! die url wo wir hin wollen liegt nicht mehr auf unserer seite
        // na dann:
        redirUrlWarning = redirUrl;
        redirUrl = new URL("/", page.url.origin);
    }
    let target = PUBLIC_API_URL + "/api/login";
    if (redirUrl != null) {
        target += "?redirect=" + encodeURIComponent(redirUrl.href);
    }
</script>

<div
    class="relative w-full md:w-md md:absolute md:left-1/2 md:top-1/2 md:-translate-1/2 flex flex-col gap-5"
>
    {#if redirUrlWarning != null}
        <Alert.Root variant="destructive">
            <CircleAlert />
            <Alert.Title
                >Die Redirect URL ist nicht auf dieser Seite!</Alert.Title
            >
            <Alert.Description>
                <p class="m-4"><code>{redirUrlWarning}</code></p>
                <p>Du wirst auf / weitergeleitet.</p>
                <p>Es könnte sein, dass jemand versucht, dich zu phishen!</p>
            </Alert.Description>
        </Alert.Root>
    {/if}
    {#if message != null}
        <Alert.Root>
            <InfoIcon></InfoIcon>
            <Alert.Description>{message}</Alert.Description>
        </Alert.Root>
    {/if}

    <form action={target} method="POST" class="flex flex-col gap-2">
        <Field.Group class="gap-5">
            <Field.Field class="grow">
                <Field.Label for="uname">Nutzername</Field.Label>
                <InputGroup.Root>
                    <InputGroup.Input
                        id="uname"
                        name="username"
                        placeholder="Nutzername"
                        required
                        autofocus
                    />
                    <InputGroup.Addon>
                        <User />
                    </InputGroup.Addon>
                </InputGroup.Root>
            </Field.Field>
            <Field.Field class="grow">
                <Field.Label for="passwd">Passwort</Field.Label>

                <InputGroup.Root>
                    <InputGroup.Input
                        id="passwd"
                    name="password"
                    placeholder="Passwort"
                    type="password"
                    required
                    autofocus
                    />
                    <InputGroup.Addon>
                        <KeyRound />
                    </InputGroup.Addon>
                </InputGroup.Root>
            </Field.Field>
            <Field.Field class="grow">
                <Button type="submit">Anmelden</Button>
            </Field.Field>
        </Field.Group>
    </form>
</div>
