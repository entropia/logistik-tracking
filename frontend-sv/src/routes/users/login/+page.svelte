<script lang="ts">
    import { PUBLIC_API_URL } from "$env/static/public";
    import { page } from "$app/state";
    import { Info as InfoIcon, CircleAlert, User, KeyRound, Car } from "@lucide/svelte";
    import * as Alert from "$lib/components/ui/alert";
    import * as Field from "$lib/components/ui/field";
    import { Button } from "$lib/components/ui/button";
    import * as InputGroup from "$lib/components/ui/input-group";
    import * as Card from "$lib/components/ui/card";

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

<svelte:head>
    <title>Anmelden</title>
</svelte:head>

<form action={target} method="POST" class="flex flex-col gap-2">
    <Card.Root class="relative w-full sm:w-md sm:absolute sm:left-1/2 sm:top-1/2 sm:-translate-1/2 flex flex-col gap-5">
        <Card.Header>
            <Card.Title>Anmelden</Card.Title>
            <Card.Description>{message}</Card.Description>
        </Card.Header>
        <Card.Content>
            {#if redirUrlWarning != null}
                <Alert.Root variant="destructive" class="mb-5">
                    <CircleAlert />
                    <Alert.Title>Die Redirect URL ist nicht auf dieser Seite!</Alert.Title>
                    <Alert.Description>
                        <p class="m-4"><code>{redirUrlWarning}</code></p>
                        <p>Du wirst auf / weitergeleitet.</p>
                        <p>Es könnte sein, dass jemand versucht, dich zu phishen!</p>
                    </Alert.Description>
                </Alert.Root>
            {/if}

            <Field.Group class="gap-5">
                <Field.Field class="grow">
                    <Field.Label for="uname">Nutzername</Field.Label>
                    <InputGroup.Root>
                        <InputGroup.Input id="uname" name="username" placeholder="Nutzername" required autofocus />
                        <InputGroup.Addon>
                            <User />
                        </InputGroup.Addon>
                    </InputGroup.Root>
                </Field.Field>
                <Field.Field class="grow">
                    <Field.Label for="passwd">Passwort</Field.Label>

                    <InputGroup.Root>
                        <InputGroup.Input id="passwd" name="password" placeholder="Passwort" type="password" required autofocus />
                        <InputGroup.Addon>
                            <KeyRound />
                        </InputGroup.Addon>
                    </InputGroup.Root>
                </Field.Field>
            </Field.Group>
        </Card.Content>
        <Card.Footer>
            <Button type="submit" class="w-full">Anmelden</Button>
        </Card.Footer>
    </Card.Root>
</form>
