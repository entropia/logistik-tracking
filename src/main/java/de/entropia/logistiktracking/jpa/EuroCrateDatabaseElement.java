package de.entropia.logistiktracking.jpa;


import de.entropia.logistiktracking.models.DeliveryState;
import de.entropia.logistiktracking.models.OperationCenter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "euro_crate", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"operation_center", "name"})
})
@DynamicUpdate
public class EuroCrateDatabaseElement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Version
	private Long version;

	@Column(name = "operation_center", nullable = false)
	private OperationCenter operationCenter;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "information", nullable = false)
	private String information;

	@Column(name = "delivery_state", nullable = false)
	private DeliveryState deliveryState;

	@Column(name = "jira_issue")
	private String jiraIssue;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "packed_crates")
	private PackingListDatabaseElement relevantList;

	public EuroCrateDatabaseElement(Long id, OperationCenter operationCenter, String name, String information, DeliveryState deliveryState, String jiraIssue) {
		this.id = id;
		this.operationCenter = operationCenter;
		this.name = name;
		this.information = information;
		this.deliveryState = deliveryState;
		this.jiraIssue = jiraIssue;
	}
}
