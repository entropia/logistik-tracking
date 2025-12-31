<script lang="ts">
	import * as Field from "$lib/components/ui/field";
	import {Input} from "$lib/components/ui/input";
	import {Button} from "$lib/components/ui/button";
    import { Save } from "@lucide/svelte";
    import { enhance } from "$app/forms";
</script>

<svelte:head>
    <title>Liste erstellen</title>
</svelte:head>

<h2 class="text-2xl mb-5 font-bold">Liste erstellen</h2>
<!-- ohne client side form shit weil das einfach simpel genug ist -->
<!-- svelte enhance für on-site redirect ohne vollen refresh -->
<form method="POST" class="w-full max-w-md" use:enhance>
    <Field.Set>
        <Field.Group>
            <Field.Field>
                <Field.Label for="name">Name</Field.Label>
                <Input id="name" type="text" name="name" placeholder="Name der Liste..." required />
            </Field.Field>
        </Field.Group>
        <Field.Field>
            <Button type="submit">
				<Save />
				Speichern
			</Button>
        </Field.Field>
    </Field.Set>
</form>