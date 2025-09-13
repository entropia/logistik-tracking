<script lang="ts">
	import {DeliveryState, OperationCenter} from "../gen/graphql";

	let {crates}: {crates: {
			deliveryState: DeliveryState;
			internalId: string;
			name: string;
			operationCenter: OperationCenter;
		}[]} = $props();
</script>

<table class="table table-auto" style="width: 100%">
    <thead>
    <tr>
        <th>ID</th>
        <th>OC</th>
        <th>Name</th>
        <th>Wo?</th>
    </tr>
    </thead>
    <tbody>
    {#each crates as crate (crate.internalId)}
        <tr>
            <td>{crate.internalId}</td>
            <td>{crate.operationCenter}</td>
            <td>
                <a class="link" href="/crates/{crate.internalId}">{crate.name}</a>
            </td>
            <td>{crate.deliveryState}</td>
        </tr>
    {/each}
    </tbody>
</table>