package de.entropia.logistiktracking.web.graphql;

import de.entropia.logistiktracking.api.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.api.converter.EuroCrateConverter;
import de.entropia.logistiktracking.api.converter.PackingListConverter;
import de.entropia.logistiktracking.api.PackingListService;
import de.entropia.logistiktracking.graphql.gen.DgsConstants;
import de.entropia.logistiktracking.graphql.gen.types.DeliveryState;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jooq.tables.records.PackingListRecord;
import de.entropia.logistiktracking.api.db.EuroCrateDatabaseService;
import de.entropia.logistiktracking.api.db.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PackingListGraphQlController {
	private final PackingListService packingListService;
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;
	private final DeliveryStateConverter deliveryStateConverter;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final EuroCrateConverter euroCrateConverter;

	@QueryMapping(DgsConstants.QUERY.GetPackingLists)
	public List<PackingList> getPackingLists() {
		return Arrays.stream(packingListDatabaseService.fetchAll())
			  .map(packingListConverter::toGraphQl)
			  .toList();
	}

	@QueryMapping(DgsConstants.QUERY.GetPackingListById)
	public PackingList getPackingListById(@Argument String id) {
		return packingListDatabaseService
			  .fetchById(Long.parseLong(id))
			  .map(packingListConverter::toGraphQl).orElse(null);
	}

	@SchemaMapping(typeName = DgsConstants.PACKINGLIST.TYPE_NAME, field = DgsConstants.PACKINGLIST.PackedCrates)
	public List<EuroCrate> getCrates(PackingList crate) {
		long id = Long.parseLong(crate.getPackingListId());
		return Arrays.stream(euroCrateDatabaseService.fetchByOwningList(id)).map(euroCrateConverter::toGraphQl).toList();
	}

	@MutationMapping(DgsConstants.MUTATION.CreatePackingList)
	public PackingList createPackingList(
		  @Argument String name
	) {
		PackingListRecord newElement = packingListService.createAndSavePackingList(de.entropia.logistiktracking.jooq.enums.DeliveryState.Packing, name);
		return packingListConverter.toGraphQl(newElement);
	}

	@MutationMapping(DgsConstants.MUTATION.SetPackingListDeliveryState)
	public PackingList setPackingListDeliveryState(
		  @Argument String id,
		  @Argument DeliveryState deliveryState
	) {
		Optional<PackingListRecord> byId = packingListDatabaseService.fetchById(Long.parseLong(id));
		if (byId.isEmpty()) return null;

		PackingListRecord updatedCrate = packingListService.updatePackingListState(deliveryStateConverter.fromGraphql(deliveryState), byId.get());
		return packingListConverter.toGraphQl(updatedCrate);
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
		euroCrateDatabaseService.leavePackingList(Long.parseLong(id));
		return packingListDatabaseService.deleteById(Long.parseLong(id)) >= 1;
	}

	@QueryMapping(DgsConstants.QUERY.GetMultipleListsById)
	public PackingList[] getMultipleCratesById(@Argument List<String> id) {
		Long[] idsToFetch = id.stream().map(Long::parseLong).toArray(Long[]::new);

		return Arrays.stream(packingListDatabaseService.multifetchById(idsToFetch))
			  .map(packingListConverter::toGraphQl)
			  .toArray(PackingList[]::new);
	}
}
