<script lang="ts">
	import { PUBLIC_API_URL } from '$env/static/public';
	import {page} from "$app/state";
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
	let target = PUBLIC_API_URL+"/api/login";
	if (redirUrl != null) {
		target += "?redirect="+encodeURIComponent(redirUrl.href);
    }
</script>

<div class="relative w-full sm:w-max sm:absolute sm:left-1/2 sm:top-1/2 sm:-translate-1/2 flex flex-col gap-2">
    {#if redirUrlWarning != null}

            <div role="alert" class="alert alert-error">
                <span class="icon-[material-symbols--warning-rounded]" style:width="48px" style:height="48px"></span>
                <div>
                    <h3 class="font-bold">Die Redirect URL ist nicht auf dieser Seite!</h3>
                    <p class="m-4"><code>{redirUrlWarning}</code></p>
                    <p>Du wirst auf / weitergeleitet.</p>
                    <p>Es könnte sein, dass jemand versucht, dich zu phishen!</p>
                </div>
            </div>
    {/if}
    {#if message != null}
        <div role="alert" class="alert alert-info">
            <span class="icon-[material-symbols--info-rounded]" style:width="24px" style:height="24px"></span>
            <span>{message}</span>
        </div>
    {/if}

    <form action="{target}" method="POST" class="flex flex-col gap-2">
        <label class="input w-full">
            <span class="icon-[material-symbols--person]" style:width="24px" style:height="24px"></span>
            <input name="username" placeholder="Nutzername" required class="grow">
        </label>
        <label class="input w-full">
            <span class="icon-[material-symbols--lock]" style:width="24px" style:height="24px"></span>
            <input name="password" placeholder="Passwort" required class="grow" type="password">
        </label>
        <button class="btn btn-success w-full" type="submit">
            Anmelden
            <span class="icon-[material-symbols--arrow-forward-rounded]" style:width="24px" style:height="24px"></span>
        </button>
    </form>
</div>