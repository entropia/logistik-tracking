package de.entropia.logistiktracking.api;


import de.entropia.logistiktracking.api.db.EuroCrateDatabaseService;
import de.entropia.logistiktracking.api.db.PackingListDatabaseService;
import de.entropia.logistiktracking.jira.JiraManager;
import de.entropia.logistiktracking.jooq.enums.DeliveryState;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional
@Component
@AllArgsConstructor
public class PackingListService {
	private final PackingListDatabaseService packingListDatabaseService;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final JiraManager jiraManager;

	public PackingListRecord createAndSavePackingList(DeliveryState state, String name) {
		PackingListRecord dbel = new PackingListRecord(null, state, name);
		return packingListDatabaseService.insert(dbel);
	}

	public PackingListRecord updatePackingListState(DeliveryState deliveryState, PackingListRecord packingListDatabaseElement) {
		packingListDatabaseElement.setDeliveryState(deliveryState);

		// all records in this array have / had an outdated state and have a jira issue
		EuroCrateRecord[] outdatedChildrenOfTheList = euroCrateDatabaseService.fetchByOwningListWithDifferentStateThanHavingJiraIssue(packingListDatabaseElement.getId(), deliveryState);

		// update all the states in bulk
		euroCrateDatabaseService.updateDeliveryStateForChildrenOf(packingListDatabaseElement.getId(), deliveryState);

		// notify relevant jira tickets that state has changed
		jiraManager.runChangeSet(List.of(outdatedChildrenOfTheList));

		return packingListDatabaseService.update(packingListDatabaseElement);
	}
}
