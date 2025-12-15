package de.entropia.logistiktracking.graphql;

import de.entropia.logistiktracking.JiraThings;
import de.entropia.logistiktracking.domain.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.packing_list.use_case.ManagePackingListUseCase;
import de.entropia.logistiktracking.graphql.gen.DgsConstants;
import de.entropia.logistiktracking.graphql.gen.types.DeliveryState;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PackingListGraphQlController {
	private final ManagePackingListUseCase managePackingListUseCase;
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;
	private final JiraThings jiraThings;
	private final DeliveryStateConverter deliveryStateConverter;
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	@QueryMapping(DgsConstants.QUERY.GetPackingLists)
	public List<PackingList> getPackingLists() {
		return managePackingListUseCase.findAllPackingLists();
	}

	@QueryMapping(DgsConstants.QUERY.GetPackingListById)
	public PackingList getPackingListById(@Argument String id) {
		return managePackingListUseCase.findPackingList(Long.parseLong(id));
	}

	@SchemaMapping(typeName = DgsConstants.PACKINGLIST.TYPE_NAME, field = DgsConstants.PACKINGLIST.PackedCrates)
	public List<EuroCrate> getCrates(PackingList crate) {
		return managePackingListUseCase.findEuroCratesOfPackingList(Long.parseLong(crate.getPackingListId()));
	}

	@MutationMapping(DgsConstants.MUTATION.CreatePackingList)
	public PackingList createPackingList(
		  @Argument String name
	) {
		PackingListRecord dbel = new PackingListRecord(null, de.entropia.logistiktracking.jooq.enums.DeliveryState.Packing, name);
		PackingListRecord newElement = packingListDatabaseService.save(dbel);
		return packingListConverter.toGraphQl(newElement);
	}

	@MutationMapping(DgsConstants.MUTATION.SetPackingListDeliveryState)
	public PackingList setPackingListDeliveryState(
		  @Argument String id,
		  @Argument DeliveryState deliveryState
	) {
		Optional<PackingListRecord> byId = packingListDatabaseService.getById(Long.parseLong(id));
		if (byId.isEmpty()) return null;
		PackingListRecord packingListDatabaseElement = byId.get();

		de.entropia.logistiktracking.jooq.enums.DeliveryState mapped = deliveryStateConverter.fromGraphql(deliveryState);
		packingListDatabaseElement.setDeliveryState(mapped);

		EuroCrateRecord[] byOwningList = euroCrateDatabaseService.getByOwningList(packingListDatabaseElement.getId());

		for (EuroCrateRecord euroCrateRecord : byOwningList) {
			if (euroCrateRecord.getDeliveryState() != mapped && euroCrateRecord.getJiraIssue() != null && !euroCrateRecord.getJiraIssue().isBlank()) {
				jiraThings.checkUpdateJiraStatus(euroCrateRecord);
			}
			euroCrateRecord.setDeliveryState(mapped);
		}

		PackingListRecord res = packingListDatabaseService.update(packingListDatabaseElement);
		return packingListConverter.toGraphQl(res);
	}

	@MutationMapping(DgsConstants.MUTATION.AddCratesToPackingList)
	@Transactional
	public PackingList addCrates(
		  @Argument String id,
		  @Argument List<String> crateIds
	) {
		euroCrateDatabaseService.joinPackingList(Long.parseLong(id), crateIds.stream().map(Long::parseLong).toList());
		return getPackingListById(id);
	}

	@MutationMapping(DgsConstants.MUTATION.RemoveCratesFromPackingList)
	@Transactional
	public PackingList removeCrates(
		  @Argument String id,
		  @Argument List<String> crateIds
	) {
		euroCrateDatabaseService.leavePackingList(Long.parseLong(id), crateIds.stream().map(Long::parseLong).toList());
		return getPackingListById(id);
	}

	@MutationMapping(DgsConstants.MUTATION.DeletePackingList)
	public boolean deletePackingList(@Argument String id) {
		packingListDatabaseService.deleteById(Long.parseLong(id));
		return true; // just return true always, we dont really care anyway
	}

	@QueryMapping(DgsConstants.QUERY.GetMultipleListsById)
	public PackingList[] getMultipleCratesById(@Argument List<String> id) {
		List<PackingList> ecs = new ArrayList<>(id.size());
		for (String s : id) {
			long actualId = Long.parseLong(s);
			Optional<PackingListRecord> byId = packingListDatabaseService.getById(actualId);
			byId
				  .map(packingListConverter::toGraphQl)
				  .ifPresent(ecs::add);
		}
		return ecs.toArray(PackingList[]::new);
	}
}
