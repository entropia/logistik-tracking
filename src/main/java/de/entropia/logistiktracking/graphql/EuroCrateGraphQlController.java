package de.entropia.logistiktracking.graphql;

import de.entropia.logistiktracking.JiraThings;
import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.euro_crate.use_case.EuroCrateUseCase;
import de.entropia.logistiktracking.domain.repository.EuroCrateRepository;
import de.entropia.logistiktracking.graphql.gen.DgsConstants;
import de.entropia.logistiktracking.graphql.gen.types.DeliveryState;
import de.entropia.logistiktracking.graphql.gen.types.EuroCrate;
import de.entropia.logistiktracking.graphql.gen.types.OperationCenter;
import de.entropia.logistiktracking.graphql.gen.types.PackingList;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.utility.Result;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@AllArgsConstructor
public class EuroCrateGraphQlController {
	private final EuroCrateUseCase euroCrateUseCase;
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final EuroCrateConverter euroCrateConverter;
	private final EuroCrateRepository euroCrateRepository;
	private final JiraThings jira;

	@QueryMapping(DgsConstants.QUERY.GetEuroCrates)
	public List<EuroCrate> getEuroCrates() {
		return euroCrateUseCase.findAllEuroCrates();
	}

	@QueryMapping(DgsConstants.QUERY.GetEuroCrateById)
	public EuroCrate getEuroCrateById(@Argument String id) {
		long actualId = Long.parseLong(id);
		Optional<EuroCrateDatabaseElement> byId = euroCrateDatabaseService.findById(actualId);
		return byId.map(euroCrateConverter::toGraphQl)
				.orElse(null);
	}

	@MutationMapping(DgsConstants.MUTATION.CreateEuroCrate)
	public EuroCrate createEuroCrate(
			@Argument String name,
			@Argument OperationCenter oc,
			@Argument DeliveryState deliveryState,
			@Argument String info,
			@Argument String jiraIssue) {
		EuroCrateDatabaseElement dbel = new EuroCrateDatabaseElement(null, de.entropia.logistiktracking.models.OperationCenter.fromGraphQl(oc),
				name,info, de.entropia.logistiktracking.models.DeliveryState.fromGraphQl(deliveryState), jiraIssue);
		EuroCrateDatabaseElement newElement = euroCrateRepository.createNewEuroCrate(dbel)
				.orElseThrow(() -> new IllegalStateException("Crate with name and oc already exists"));
		return euroCrateConverter.toGraphQl(newElement);
	}

	@MutationMapping(DgsConstants.MUTATION.DeleteEuroCrate)
	public boolean deleteEuroCrate(@Argument String id) {
		euroCrateDatabaseService.deleteById(Long.valueOf(id));
		return true; // just return true always, we dont really care anyway
	}

	// todo: modify
	@MutationMapping(DgsConstants.MUTATION.ModifyEuroCrate)
	public EuroCrate modifyCrate(
			@Argument String id,
			@Argument OperationCenter oc,
			@Argument DeliveryState deliveryState,
			@Argument String info,
			@Argument String jiraIssue
	) {
		Optional<EuroCrateDatabaseElement> found = euroCrateRepository.findEuroCrate(Long.parseLong(id));
		if (found.isEmpty()) return null;
		EuroCrateDatabaseElement the = found.get();
		the.setOperationCenter(de.entropia.logistiktracking.models.OperationCenter.fromGraphQl(oc));
		DeliveryState oldStatus = the.getDeliveryState().graphQlEquiv;
		the.setDeliveryState(de.entropia.logistiktracking.models.DeliveryState.fromGraphQl(deliveryState));
		the.setJiraIssue(jiraIssue);
		EuroCrateDatabaseElement updated = euroCrateDatabaseService.save(the);

		if (oldStatus != deliveryState) {
			// update ticket state or add note
			// do last to flush changes to db first
			jira.checkUpdateJiraStatus(updated);
		}

		return euroCrateConverter.toGraphQl(updated);
	}

	@SchemaMapping(typeName = DgsConstants.EUROCRATE.TYPE_NAME, field = DgsConstants.EUROCRATE.PackingList)
	public PackingList getPackingList(EuroCrate crate) {
		Result<PackingList, EuroCrateUseCase.FindRelatedPackingListError> packingListsOfCrate = euroCrateUseCase.getPackingListsOfCrate(Long.parseLong(crate.getInternalId()));
		if (packingListsOfCrate instanceof Result.Ok<PackingList, EuroCrateUseCase.FindRelatedPackingListError>(var bruh)) {
			return bruh;
		}
		return null;
	}
}
