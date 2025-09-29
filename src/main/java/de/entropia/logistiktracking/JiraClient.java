package de.entropia.logistiktracking;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Objects;

@Configuration
public class JiraClient {
	@Bean
	public JiraRestClient bJiraClient(
			@Value("${logitrack.jiraUrl}") String jiraUrl,
			@Value("#{environment.JIRA_KEY}") String jiraKey
	) {
		Objects.requireNonNull(jiraKey, "Jira key is not defined. Please define JIRA_KEY env");
		return new AsynchronousJiraRestClientFactory()
			.createWithAuthenticationHandler(URI.create(jiraUrl), builder -> {
				builder.setHeader("Authorization", "Bearer "+jiraKey);
			});
	}
}
