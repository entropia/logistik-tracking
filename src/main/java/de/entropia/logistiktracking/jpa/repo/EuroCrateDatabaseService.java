package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EuroCrateDatabaseService extends JpaRepository<EuroCrateDatabaseElement, Long> {
	List<EuroCrateDatabaseElement> findAllByJiraIssue(String jiraIssue);
}
