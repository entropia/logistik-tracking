package de.entropia.logistiktracking.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.google.common.collect.Iterables;
import de.entropia.logistiktracking.jooq.enums.DeliveryState;
import de.entropia.logistiktracking.jooq.tables.records.EuroCrateRecord;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class JiraManager {
	private EuroCrateDatabaseService ecdbs;

	private JiraRestClient jrc;

	private static final String MESSAGE_PACKING_STATE = "[Logitrack] Die Kiste(n) %s, verbunden mit diesem Ticket, wurden zurück auf *Packing* gesetzt.";
	private static final String MESSAGE_ERROR_CANT_FIND_TRANSITION = "[Logitrack] Die Kiste(n) %s, verbunden mit diesem Ticket, wurden auf '%s' gesetzt, allerdings kann die gewollte Transition '%s' nicht auf diesem Ticket aufgerufen werden. Bitte manuell korrigieren.";
	private static final String MESSAGE_TRANSPORT_STATE = "[Logitrack] Die Kiste(n) %s, verbunden mit diesem Ticket, wurden auf 'Transport' gesetzt. Gleich gehts los!";

	private static final String MESSAGE_NOT_ALL_IN_SYNC = "[Logitrack] Die Kiste(n) %s, verbunden mit diesem Ticket, wurden auf '%s' gesetzt. Es gibt noch andere Kisten, die teilweise andere Stati haben. Alle Kisten:";

	@Async
	public void runChangeSet(List<EuroCrateRecord> modifiedCrates) {
		Map<String, List<EuroCrateRecord>> keyToCrate = modifiedCrates.stream()
			  .map(EuroCrateRecord::getJiraIssue)
			  .distinct()
			  .collect(Collectors.toMap(jiraKey -> jiraKey,
					jiraKey -> List.of(ecdbs.fetchByJiraIssue(jiraKey)),
					(a, b) -> b));

		for (Map.Entry<String, List<EuroCrateRecord>> jiraIssueToCrates : keyToCrate.entrySet()) {
			List<EuroCrateRecord> modifiedCratesWithThisJira = modifiedCrates.stream().filter(f -> f.getJiraIssue().equals(jiraIssueToCrates.getKey())).toList();
			checkCanTransitionTicket(jiraIssueToCrates.getKey(), jiraIssueToCrates.getValue(), modifiedCratesWithThisJira);
		}
	}

	private void checkCanTransitionTicket(String jiraKey, List<EuroCrateRecord> allCratesWithThisIssue, List<EuroCrateRecord> modifiedCratesThisRound) {
		EuroCrateRecord refCrate = allCratesWithThisIssue.getFirst();
		DeliveryState refState = refCrate.getDeliveryState();

		Issue issueInstance = jrc.getIssueClient().getIssue(jiraKey).claim();
		// alle kisten müssen targetDeliveryState haben damit wir aktualisieren können - ansonsten notiz
		if (allCratesWithThisIssue.stream().allMatch(it -> it.getDeliveryState() == refState)) {
			// wir können das ticket updaten, je nachdem welcher neuer status wir jetzt haben
			switch (refState) {
				case Packing -> {
					// No state change, in general this should probably not happen because this is the initial status
					// We shouldn't have to modify a crate to get back here
					// Warn i guess?
					addComment(issueInstance, MESSAGE_PACKING_STATE.formatted(formatCrateList(modifiedCratesThisRound)));
				}
				case WaitingForTransport, AtHome -> {
					// Want state change: 'Disponieren'
					Iterable<Transition> transitions = jrc.getIssueClient().getTransitions(issueInstance).claim();
					Optional<Transition> dispTransition = StreamSupport.stream(transitions.spliterator(), false)
						  .filter(f -> f.getName().equals("Disponieren") && Iterables.isEmpty(f.getFields())).findAny();
					if (dispTransition.isEmpty()) {
						addComment(issueInstance, MESSAGE_ERROR_CANT_FIND_TRANSITION.formatted(formatCrateList(modifiedCratesThisRound), refState.name(), "Disponieren"));
					} else {
						transitionIssue(issueInstance, dispTransition.get());
					}
				}
				case Transport -> {
					// No state change, notify that the box is now in transport
					addComment(issueInstance, MESSAGE_TRANSPORT_STATE.formatted(formatCrateList(modifiedCratesThisRound)));
				}
				case AtGpn -> {
					// Want state change: @GPN angekommen
					Iterable<Transition> transitions = jrc.getIssueClient().getTransitions(issueInstance).claim();
					Optional<Transition> dispTransition = StreamSupport.stream(transitions.spliterator(), false)
						  .filter(f -> f.getName().equals("@GPN angekommen") && Iterables.isEmpty(f.getFields())).findAny();
					if (dispTransition.isEmpty()) {
						addComment(issueInstance, MESSAGE_ERROR_CANT_FIND_TRANSITION.formatted(formatCrateList(modifiedCratesThisRound), refState.name(), "@GPN angekommen"));
					} else {
						transitionIssue(issueInstance, dispTransition.get());
					}
				}
			}
		} else {
			// nicht alle crates sind auf dem selben status
			String baseString = MESSAGE_NOT_ALL_IN_SYNC.formatted(formatCrateList(modifiedCratesThisRound), refState.name());
			StringBuilder sb = new StringBuilder(baseString);
			for (EuroCrateRecord euroCrateRecord : allCratesWithThisIssue) {
				sb.append("\n").append("*").append(euroCrateRecord.getOperationCenter().getLiteral()).append(" / ").append(euroCrateRecord.getName()).append("*: ")
					  .append(euroCrateRecord.getDeliveryState().getLiteral());
			}
			addComment(issueInstance, sb.toString());
		}
	}

	private String formatCrateList(List<EuroCrateRecord> modifiedCratesThisRound) {
		return modifiedCratesThisRound.stream().map(it -> "*%s / %s* (%d)".formatted(it.getOperationCenter().getLiteral(), it.getName(), it.getId())).collect(Collectors.joining(", "));
	}

	private void transitionIssue(Issue issueInstance, Transition dispTransition) {
//		log.info("Transition {}: -> \n{}", issueInstance.getKey(), dispTransition.getName());
		jrc.getIssueClient().transition(issueInstance, new TransitionInput(dispTransition.getId()));
	}

	private void addComment(Issue issueToComment, String comment) {
//		log.info("Comment {}:\n{}", issueToComment.getKey(), comment);
		jrc.getIssueClient().addComment(issueToComment.getCommentsUri(), Comment.valueOf(comment));
	}
}
