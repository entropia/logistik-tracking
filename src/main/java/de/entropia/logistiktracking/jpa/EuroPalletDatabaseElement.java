package de.entropia.logistiktracking.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "euro_pallet")
@DynamicUpdate
public class EuroPalletDatabaseElement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pallet_id", nullable = false)
	private Long palletId;

	@Version
	private Long version;

	@Column(name = "information", nullable = false)
	private String palleteName;

	@Embedded
	private LocationDatabaseElement location;

	public EuroPalletDatabaseElement(Long palletId, String palleteName, LocationDatabaseElement location) {
		this.palletId = palletId;
		this.palleteName = palleteName;
		this.location = location;
	}
}
