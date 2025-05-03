package de.entropia.logistiktracking.jpa;


import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "packing_list")
public class PackingListDatabaseElement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "packing_list_id", nullable = false)
	private long packingListId;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "delivery_state")
	private DeliveryState deliveryState;

	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "packed_on", nullable = false)
	private EuroPalletDatabaseElement packedOn;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "packed_crates")
	private List<EuroCrateDatabaseElement> packedCrates;
}
