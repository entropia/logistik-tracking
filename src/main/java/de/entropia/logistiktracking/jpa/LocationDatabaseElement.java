package de.entropia.logistiktracking.jpa;

import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
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
@Table(name = "location")
public class LocationDatabaseElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id", nullable = false)
    private long id;

    @Column(name = "logistics_location")
    private LogisticsLocationType logisticsLocation;

    @Column(name = "operation_center")
    private OperationCenter operationCenter;

    @Column(name = "somewhereElse")
    private String somewhereElse;
}
