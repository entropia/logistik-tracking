package de.entropia.logistiktracking.domain.packing_list;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.domain.euro_pallet.EuroPallet;

import java.util.Collections;
import java.util.List;


public class PackingListBuilder {
	private long packingListId;
	private String name;
	private DeliveryState deliveryState;
	private EuroPallet packedOn;
	private List<EuroCrate> packedCrates;

	PackingListBuilder() {
		this.packingListId = 0;
		this.name = null;
		this.deliveryState = DeliveryState.Packing;
		this.packedOn = null;
	}

	public PackingListBuilder packingListId(long packingListId) {
		this.packingListId = packingListId;
		return this;
	}

	public PackingListBuilder name(String name) {
		this.name = name;
		return this;
	}

	public PackingListBuilder deliveryState(DeliveryState deliveryState) {
		this.deliveryState = deliveryState;
		return this;
	}

	public PackingListBuilder packedOn(EuroPallet packedOn) {
		this.packedOn = packedOn;
		return this;
	}

	public PackingListBuilder packedCrates(List<EuroCrate> packedCrates) {
		this.packedCrates = packedCrates;
		return this;
	}

	public PackingList build() throws IllegalArgumentException {
		if (packingListId < 0) {
			throw new IllegalArgumentException("Packing list id cannot be negative");
		}
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("name has to be set and cannot be empty");
		}
		if (deliveryState == null) {
			throw new IllegalArgumentException("deliveryState has to be set");
		}
		if (packedOn == null) {
			throw new IllegalArgumentException("packedOn has to be set");
		}
		return new PackingList(packingListId, name, deliveryState, packedOn, packedCrates == null ? Collections.emptyList() : packedCrates);
	}
}
