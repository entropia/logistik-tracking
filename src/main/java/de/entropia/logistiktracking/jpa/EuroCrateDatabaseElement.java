package de.entropia.logistiktracking.jpa;


import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@IdClass(EuroCrateDatabaseElement.EuroCrateDatabaseElementId.class)
@Table(name = "euro_crate")
public class EuroCrateDatabaseElement {
    @Id
    @Column(name = "operationCenter", nullable = false)
    private OperationCenter operationCenter;

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "returnBy", nullable = false)
    private LocalDate returnBy;

    @Column(name = "information", nullable = false)
    private String information;

    @Column(name = "deliveryState", nullable = false)
    private DeliveryState deliveryState;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "location", nullable = false)
    private LocationDatabaseElement location;

    @Setter
    @AllArgsConstructor
    public static class EuroCrateDatabaseElementId {
        private OperationCenter operationCenter;
        private String name;

        public EuroCrateDatabaseElementId() {
            operationCenter = null;
            name = null;
        }
    }
}
