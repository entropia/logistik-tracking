package de.entropia.logistiktracking.api;

import de.entropia.logistiktracking.jira.JiraManager;
import de.entropia.logistiktracking.jooq.enums.DeliveryState;
import de.entropia.logistiktracking.jooq.enums.OperationCenter;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import de.entropia.logistiktracking.api.db.EuroCrateDatabaseService;
import de.entropia.logistiktracking.api.db.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class EuroCrateService {
	private final PackingListDatabaseService packingListDatabaseService;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final JiraManager jiraManager;

	public Optional<PackingListRecord> getPackingListsOfCrate(long id) {
		Optional<EuroCrateRecord> euroCrate = euroCrateDatabaseService.fetchById(id);
		if (euroCrate.isEmpty()) throw new IllegalStateException("crate not found");
		EuroCrateRecord ec = euroCrate.get();

		return Optional.ofNullable(ec.getOwningList())
			  .flatMap(packingListDatabaseService::fetchById);
	}

	public EuroCrateRecord createAndInsertCrate(String name, OperationCenter oc, DeliveryState deliveryState, String info, String jiraIssue) {
		EuroCrateRecord euroCrateRecord = new EuroCrateRecord(null, deliveryState, info, jiraIssue, name, oc, null);
		return euroCrateDatabaseService.insert(
			  euroCrateRecord
		);
	}

	public EuroCrateRecord updateAndSaveCrate(EuroCrateRecord crateToUpdate, OperationCenter oc, DeliveryState deliveryState, String info, String jiraIssue) {
		// todo lazy updates
		crateToUpdate.setOperationCenter(oc);
		crateToUpdate.setJiraIssue(jiraIssue);
		crateToUpdate.setInformation(info);
		DeliveryState oldDeliState = crateToUpdate.getDeliveryState();
		crateToUpdate.setDeliveryState(deliveryState);

		EuroCrateRecord updated = euroCrateDatabaseService.update(crateToUpdate);

		if (oldDeliState != deliveryState && updated.getJiraIssue() != null) {
			// update ticket state or add note
			// do last to flush changes to db first
			jiraManager.runChangeSet(List.of(updated));
		}

		return updated;
	}
}
