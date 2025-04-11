package de.entropia.logistiktracking.jpa;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "euro_pallet")
public class EuroPalletDatabaseElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pallet_id", nullable = false)
    private long palletId;

    @Column(name = "information", nullable = false)
    private String information;

    @Embedded
    private LocationDatabaseElement location;
}
