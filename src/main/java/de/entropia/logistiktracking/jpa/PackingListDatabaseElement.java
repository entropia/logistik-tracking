package de.entropia.logistiktracking.jpa;


import de.entropia.logistiktracking.models.DeliveryState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "packing_list")
@DynamicUpdate
public class PackingListDatabaseElement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "packing_list_id", nullable = false)
	private Long packingListId;

	@Version
	private Long version;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "delivery_state", nullable = false)
	private DeliveryState deliveryState;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "relevantList")
	private List<EuroCrateDatabaseElement> packedCrates;

	public PackingListDatabaseElement(Long packingListId, String name, DeliveryState deliveryState, List<EuroCrateDatabaseElement> packedCrates) {
		this.packingListId = packingListId;
		this.name = name;
		this.deliveryState = deliveryState;
		this.packedCrates = packedCrates;
	}
}
