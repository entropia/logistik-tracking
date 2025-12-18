package de.entropia.logistiktracking.graphql;

import de.entropia.logistiktracking.JiraThings;
import de.entropia.logistiktracking.domain.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.OperationCenterConverter;
import de.entropia.logistiktracking.domain.euro_crate.use_case.EuroCrateUseCase;
import de.entropia.logistiktracking.graphql.gen.DgsConstants;
import de.entropia.logistiktracking.graphql.gen.types.DeliveryState;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.OperationCenter;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
public class EuroCrateGraphQlController {
	private final EuroCrateUseCase euroCrateUseCase;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final EuroCrateConverter euroCrateConverter;
	private final JiraThings jira;
	private final OperationCenterConverter operationCenterConverter;
	private final DeliveryStateConverter deliveryStateConverter;

	@QueryMapping(DgsConstants.QUERY.GetEuroCrates)
	public List<EuroCrate> getEuroCrates() {
		return euroCrateUseCase.findAllEuroCrates();
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
		EuroCrateRecord euroCrateRecord = new EuroCrateRecord(null, deliveryStateConverter.fromGraphql(deliveryState), info, jiraIssue, name, operationCenterConverter.fromGraphql(oc), null);
		EuroCrateRecord returned = euroCrateDatabaseService.insert(
			  euroCrateRecord
		);
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
		the.setOperationCenter(operationCenterConverter.fromGraphql(oc));
		the.setJiraIssue(jiraIssue);
		the.setInformation(info);
		DeliveryState oldStatus = deliveryStateConverter.toGraphql(the.getDeliveryState());
		the.setDeliveryState(deliveryStateConverter.fromGraphql(deliveryState));

		EuroCrateRecord updated = euroCrateDatabaseService.update(the);

		if (oldStatus != deliveryState) {
			// update ticket state or add note
			// do last to flush changes to db first
			jira.checkUpdateJiraStatus(updated);
		}

		return euroCrateConverter.toGraphQl(updated);
	}

	@SchemaMapping(typeName = DgsConstants.EUROCRATE.TYPE_NAME, field = DgsConstants.EUROCRATE.PackingList)
	public PackingList getPackingList(EuroCrate crate) {
		return euroCrateUseCase.getPackingListsOfCrate(Long.parseLong(crate.getInternalId()));
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
