package de.entropia.logistiktracking;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.google.common.collect.Iterables;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
@Slf4j
public class JiraThings {
	private final JiraRestClient jrc;

	private static final String MESSAGE_PACKING_STATE = "[Logitrack] Eine Eurokiste '*%s / %s*' (ID %d) verbunden mit diesem Ticket wurde zurürck auf '*Packing*' gesetzt.";
	private static final String MESSAGE_ERROR_CANT_FIND_TRANSITION = "[Logitrack] Eine Eurokiste '*%s / %s*' (ID %d) verbunden mit diesem Ticket wurde auf '%s' gesetzt, allerdings kann die gewollte Transition '%s' nicht auf diesem Ticket aufgerufen werden. Bitte manuell korrigieren.";
	private static final String MESSAGE_TRANSPORT_STATE = "[Logitrack] Eine Eurokiste '*%s / %s*' (ID %d) verbunden mit diesem Ticket wurde auf 'Transport' gesetzt. Gleich gehts los!";

	private static final String MESSAGE_NOT_ALL_IN_SYNC = "[Logitrack] Eine Eurokiste '*%s / %s*' (ID %d) verbunden mit diesem Ticket wurde auf '%s' gesetzt. Es gibt noch %d andere Kisten, die teilweise andere Stati haben.";
	private final EuroCrateDatabaseService euroCrateDatabaseService;

	@Async
	public void checkUpdateJiraStatus(EuroCrateDatabaseElement ticketThatWasChanged) {
		List<EuroCrateDatabaseElement> allByJiraIssue = euroCrateDatabaseService.findAllByJiraIssue(ticketThatWasChanged.getJiraIssue());
		assert !allByJiraIssue.isEmpty() : "keine boxen die dem jira issue entsprechen?";
		de.entropia.logistiktracking.models.DeliveryState targetDeliveryState = ticketThatWasChanged.getDeliveryState();
		String issue = ticketThatWasChanged.getJiraIssue();
		Issue issueInstance = jrc.getIssueClient().getIssue(issue).claim();
		 log.info("Resolved issue key {} for crate {}: {}", issue, ticketThatWasChanged.getId(), issueInstance.getCommentsUri());
		// alle kisten müssen targetDeliveryState haben damit wir aktualisieren können - ansonsten notiz
		if (allByJiraIssue.stream().allMatch(it -> it.getDeliveryState() == targetDeliveryState)) {
			// wir können das ticket updaten, je nachdem welcher neuer status wir jetzt haben
			switch (targetDeliveryState) {
				case Packing -> {
					// No state change, in general this should probably not happen because this is the initial status
					// We shouldn't have to modify a crate to get back here
					// Warn i guess?
					jrc.getIssueClient().addComment(issueInstance.getCommentsUri(), Comment.valueOf(MESSAGE_PACKING_STATE.formatted(ticketThatWasChanged.getOperationCenter(), ticketThatWasChanged.getName(), ticketThatWasChanged.getId())));
				}
				case WaitingForTransport, AtHome -> {
					// Want state change: 'Disponieren'
					Iterable<Transition> transitions = jrc.getIssueClient().getTransitions(issueInstance).claim();
					Optional<Transition> dispTransition = StreamSupport.stream(transitions.spliterator(), false)
							.filter(f -> f.getName().equals("Disponieren") && Iterables.isEmpty(f.getFields())).findAny();
					if (dispTransition.isEmpty()) {
						jrc.getIssueClient().addComment(issueInstance.getCommentsUri(),
								Comment.valueOf(MESSAGE_ERROR_CANT_FIND_TRANSITION.formatted(ticketThatWasChanged.getOperationCenter(), ticketThatWasChanged.getName(), ticketThatWasChanged.getId(), targetDeliveryState.name(), "Disponieren")));
					} else {
						jrc.getIssueClient().transition(issueInstance, new TransitionInput(dispTransition.get().getId()));
					}
				}
				case Transport -> {
					// No state change, notify that the box is now in transport
					jrc.getIssueClient().addComment(issueInstance.getCommentsUri(), Comment.valueOf(MESSAGE_TRANSPORT_STATE.formatted(ticketThatWasChanged.getOperationCenter(), ticketThatWasChanged.getName(), ticketThatWasChanged.getId())));
				}
				case AtGpn -> {
					// Want state change: @GPN angekommen
					Iterable<Transition> transitions = jrc.getIssueClient().getTransitions(issueInstance).claim();
					Optional<Transition> dispTransition = StreamSupport.stream(transitions.spliterator(), false)
							.filter(f -> f.getName().equals("@GPN angekommen") && Iterables.isEmpty(f.getFields())).findAny();
					if (dispTransition.isEmpty()) {
						jrc.getIssueClient().addComment(issueInstance.getCommentsUri(),
								Comment.valueOf(MESSAGE_ERROR_CANT_FIND_TRANSITION.formatted(ticketThatWasChanged.getOperationCenter(), ticketThatWasChanged.getName(), ticketThatWasChanged.getId(), targetDeliveryState.name(), "@GPN angekommen")));
					} else {
						jrc.getIssueClient().transition(issueInstance, new TransitionInput(dispTransition.get().getId()));
					}
				}
			}
		} else {
			// nicht alle crates sind auf dem selben status
			jrc.getIssueClient().addComment(issueInstance.getCommentsUri(), Comment.valueOf(MESSAGE_NOT_ALL_IN_SYNC.formatted(ticketThatWasChanged.getOperationCenter(), ticketThatWasChanged.getName(), ticketThatWasChanged.getId(), targetDeliveryState.name(), allByJiraIssue.size() - 1)));
		}
	}
}
