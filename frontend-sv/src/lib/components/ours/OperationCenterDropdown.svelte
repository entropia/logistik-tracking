<script lang="ts">
    import type {WithoutChild} from "$lib/utils";
	import {Select as SelPrimitive} from "bits-ui";
	import {OperationCenter} from "../../../gen/graphql";
	import * as Select from "../ui/select";

	type TheProps = {
		value: OperationCenter
    } & WithoutChild<SelPrimitive.TriggerProps> & {
		size?: "sm" | "default";
	};
    let {
		value = $bindable(),
        ...restProps
    }: TheProps = $props();
</script>

<Select.Root type="single" bind:value={value}>
    <Select.Trigger {...restProps}>{value}</Select.Trigger>
    <Select.Content>
        {#each Object.entries(OperationCenter) as oc}
            <Select.Item value={oc[0]}>{oc[1]}</Select.Item>
        {/each}
    </Select.Content>
</Select.Root>