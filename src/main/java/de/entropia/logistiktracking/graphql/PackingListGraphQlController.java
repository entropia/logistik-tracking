package de.entropia.logistiktracking.graphql;

import de.entropia.logistiktracking.domain.converter.PackingListConverter;
import de.entropia.logistiktracking.domain.packing_list.use_case.ManagePackingListUseCase;
import de.entropia.logistiktracking.domain.repository.PackingListRepository;
import de.entropia.logistiktracking.graphql.gen.DgsConstants;
import de.entropia.logistiktracking.graphql.gen.types.DeliveryState;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.PackingListDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.PackingListDatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class PackingListGraphQlController {
	private final ManagePackingListUseCase managePackingListUseCase;
	private final PackingListDatabaseService packingListDatabaseService;
	private final PackingListConverter packingListConverter;

	@QueryMapping(DgsConstants.QUERY.GetPackingLists)
	public List<PackingList> getPackingLists() {
		return managePackingListUseCase.findAllPackingLists();
	}

	@QueryMapping(DgsConstants.QUERY.GetPackingListById)
	public PackingList getPackingListById(@Argument String id) {
		return managePackingListUseCase.findPackingList(Long.parseLong(id))
				.optional()
				.orElse(null);
	}

	@SchemaMapping(typeName = DgsConstants.PACKINGLIST.TYPE_NAME, field = DgsConstants.PACKINGLIST.PackedCrates)
	public List<EuroCrate> getCrates(PackingList crate) {
		return managePackingListUseCase.findEuroCratesOfPackingList(Long.parseLong(crate.getPackingListId()));
	}

	// todo create

	@MutationMapping(DgsConstants.MUTATION.SetPackingListDeliveryState)
	public PackingList setPackingListDeliveryState(
			@Argument String id,
			@Argument DeliveryState deliveryState
			) {
		Optional<PackingListDatabaseElement> byId = packingListDatabaseService.findById(Long.parseLong(id));
		if (byId.isEmpty()) return null;
		PackingListDatabaseElement packingListDatabaseElement = byId.get();
		de.entropia.logistiktracking.models.DeliveryState mapped = de.entropia.logistiktracking.models.DeliveryState.fromGraphQl(deliveryState);
		packingListDatabaseElement.setDeliveryState(mapped);
		for (EuroCrateDatabaseElement packedCrate : packingListDatabaseElement.getPackedCrates()) {
			packedCrate.setDeliveryState(mapped);
		}
		PackingListDatabaseElement res = packingListDatabaseService.save(packingListDatabaseElement);
		return packingListConverter.toGraphQl(res);
	}

	@MutationMapping(DgsConstants.MUTATION.AddCratesToPackingList)
	@Transactional
	public PackingList addCrates(
			@Argument String id,
			@Argument List<String> crateIds
	) {
		packingListDatabaseService.addCrateToPackingListReassignIfAlreadyAssigned(Long.parseLong(id), crateIds.stream().map(Long::parseLong).toList());
		return getPackingListById(id);
	}

	@MutationMapping(DgsConstants.MUTATION.RemoveCratesFromPackingList)
	@Transactional
	public PackingList removeCrates(
			@Argument String id,
			@Argument List<String> crateIds
	) {
		packingListDatabaseService.removeCrateFromPackingList(Long.parseLong(id), crateIds.stream().map(Long::parseLong).toList());
		return getPackingListById(id);
	}

	// todo delete
}
