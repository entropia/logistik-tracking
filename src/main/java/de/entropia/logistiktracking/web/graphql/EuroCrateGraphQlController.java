package de.entropia.logistiktracking.web.graphql;

import de.entropia.logistiktracking.api.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.api.converter.EuroCrateConverter;
import de.entropia.logistiktracking.api.converter.OperationCenterConverter;
import de.entropia.logistiktracking.api.converter.PackingListConverter;
import de.entropia.logistiktracking.api.EuroCrateService;
import de.entropia.logistiktracking.graphql.gen.DgsConstants;
import de.entropia.logistiktracking.graphql.gen.types.DeliveryState;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.OperationCenter;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.api.db.EuroCrateDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
public class EuroCrateGraphQlController {
	private final EuroCrateService euroCrateService;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final EuroCrateConverter euroCrateConverter;
	private final OperationCenterConverter operationCenterConverter;
	private final DeliveryStateConverter deliveryStateConverter;
	private final PackingListConverter packingListConverter;

	@QueryMapping(DgsConstants.QUERY.GetEuroCrates)
	public List<EuroCrate> getEuroCrates() {
		return Arrays.stream(euroCrateDatabaseService.fetchAll())
			  .map(euroCrateConverter::toGraphQl)
			  .toList();
	}

	@QueryMapping(DgsConstants.QUERY.GetEuroCrateById)
	public EuroCrate getEuroCrateById(@Argument String id) {
		long actualId = Long.parseLong(id);
		return euroCrateDatabaseService.fetchById(actualId).map(euroCrateConverter::toGraphQl).orElse(null);
	}

	@MutationMapping(DgsConstants.MUTATION.CreateEuroCrate)
	public EuroCrate createEuroCrate(
		  @Argument String name,
		  @Argument OperationCenter oc,
		  @Argument DeliveryState deliveryState,
		  @Argument String info,
		  @Argument String jiraIssue) {
		EuroCrateRecord returned = euroCrateService.createAndInsertCrate(name, operationCenterConverter.fromGraphql(oc), deliveryStateConverter.fromGraphql(deliveryState), info, jiraIssue);
		return euroCrateConverter.toGraphQl(returned);
	}

	@MutationMapping(DgsConstants.MUTATION.DeleteEuroCrate)
	public boolean deleteEuroCrate(@Argument String id) {
		euroCrateDatabaseService.deleteById(Long.parseLong(id));
		return true; // just return true always, we dont really care anyway
	}

	@MutationMapping(DgsConstants.MUTATION.ModifyEuroCrate)
	public EuroCrate modifyCrate(
		  @Argument String id,
		  @Argument OperationCenter oc,
		  @Argument DeliveryState deliveryState,
		  @Argument String info,
		  @Argument String jiraIssue
	) {
		Optional<EuroCrateRecord> found = euroCrateDatabaseService.fetchById(Long.parseLong(id));
		if (found.isEmpty()) return null;

		EuroCrateRecord the = found.get();
		// fixme does it make more sense to pass the id or the object? i think id since we explicitly want to update something already in the db
		EuroCrateRecord updated = euroCrateService.updateAndSaveCrate(the, operationCenterConverter.fromGraphql(oc), deliveryStateConverter.fromGraphql(deliveryState), info, jiraIssue);

		return euroCrateConverter.toGraphQl(updated);
	}

	@SchemaMapping(typeName = DgsConstants.EUROCRATE.TYPE_NAME, field = DgsConstants.EUROCRATE.PackingList)
	public PackingList getPackingList(EuroCrate crate) {
		return euroCrateService.getPackingListsOfCrate(Long.parseLong(crate.getInternalId()))
			  .map(packingListConverter::toGraphQl)
			  .orElse(null);
	}

	@QueryMapping(DgsConstants.QUERY.GetMultipleCratesById)
	public EuroCrate[] getMultipleCratesById(@Argument List<String> id) {
		List<EuroCrate> ecs = new ArrayList<>(id.size());
		for (String s : id) {
			long actualId = Long.parseLong(s);
			Optional<EuroCrateRecord> byId = euroCrateDatabaseService.fetchById(actualId);
			byId
				  .map(euroCrateConverter::toGraphQl)
				  .ifPresent(ecs::add);
		}
		return ecs.toArray(EuroCrate[]::new);
	}
}
