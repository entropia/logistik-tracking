package de.entropia.logistiktracking.jpa;


import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@IdClass(PackingListDatabaseElement.PackingListDatabaseElementId.class)
@Table(name = "packing_list")
public class PackingListDatabaseElement {
    @Id
    @GeneratedValue
    @Column(name = "packing_list_id", nullable = false)
    private long packingListId;

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "delivery_state")
    private DeliveryState deliveryState;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "packed_on", nullable = false)
    private EuroPalletDatabaseElement packedOn;

    @Setter
    public static class PackingListDatabaseElementId {
        private long packingListId;
        private String name;

        public PackingListDatabaseElementId() {
            this.packingListId = 0;
            this.name = "";
        }
    }

}
