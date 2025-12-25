<script lang="ts" generics="T extends Record<string, unknown>">
    import type { HTMLAttributes } from "svelte/elements";
    import * as Field from "$lib/components/ui/field";
    import {
        formFieldProxy,
        type SuperForm,
        type FormPathLeaves,
        type InputConstraint,
    } from "sveltekit-superforms";
    import type { WithElementRef } from "$lib/utils";
    import type { FieldOrientation } from "../ui/field/field.svelte";
    import type { Snippet } from "svelte";

    type fFieldProps = WithElementRef<HTMLAttributes<HTMLDivElement>> & {
        orientation?: FieldOrientation;
    };

    type InpPropsType = InputConstraint & {
                name?: string | null;
                "aria-invalid": HTMLAttributes<HTMLElement>["aria-invalid"];
                id?: string;
            };

    type Props = {
        superform: SuperForm<T>;
        field: FormPathLeaves<T>;
        children: Snippet<[{
            labProps: {"for"?: string | null},
            inpProps: InpPropsType
        }]>;
    } & Omit<fFieldProps, "children">;

    const uid = $props.id();

    let { superform, field, children, ...rest }: Props = $props();

    const { errors, constraints } = formFieldProxy(superform, field);

    let inputProps = $derived.by(() => ({
        name: field,
        "aria-invalid": $errors ? "true" : undefined,
        id: uid+"-inp",
        ...$constraints
    })) satisfies InpPropsType
</script>

<Field.Field {...rest}>
    {@render children({inpProps: inputProps, labProps: {
        "for": uid+"-inp"
    }})}
    {#if $errors}
        <Field.Error>{$errors}</Field.Error>
    {/if}
</Field.Field>
