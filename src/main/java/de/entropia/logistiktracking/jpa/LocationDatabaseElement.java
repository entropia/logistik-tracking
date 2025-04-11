package de.entropia.logistiktracking.jpa;

import de.entropia.logistiktracking.domain.location.LogisticsLocationType;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class LocationDatabaseElement {


    @Column(name = "location_loc")
    private LogisticsLocationType logisticsLocation;

    @Column(name = "location_operation_center")
    private OperationCenter operationCenter;

    @Column(name = "location_somewhereElse")
    private String somewhereElse;
}
