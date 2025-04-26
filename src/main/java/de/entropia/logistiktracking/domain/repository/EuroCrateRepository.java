package de.entropia.logistiktracking.domain.repository;

import de.entropia.logistiktracking.domain.converter.DeliveryStateConverter;
import de.entropia.logistiktracking.domain.converter.EuroCrateConverter;
import de.entropia.logistiktracking.domain.converter.LocationConverter;
import de.entropia.logistiktracking.domain.euro_crate.EuroCrate;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement_;
import de.entropia.logistiktracking.jpa.repo.EuroCrateDatabaseService;
import de.entropia.logistiktracking.openapi.model.EuroCratePatchDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class EuroCrateRepository {
	private final EuroCrateDatabaseService euroCrateDatabaseService;
	private final EuroCrateConverter euroCrateConverter;

	private final EntityManager em;
	private final DeliveryStateConverter deliveryStateConverter;
	private final LocationConverter locationConverter;

	public Optional<EuroCrate> createNewEuroCrate(EuroCrate euroCrate) {
		if (euroCrateDatabaseService.existsByOperationCenterAndName(
				euroCrate.getOperationCenter(),
				euroCrate.getName()
		)) {
			return Optional.empty();
		}

		EuroCrateDatabaseElement newCrateElement = euroCrateDatabaseService.save(
				euroCrateConverter.toDatabaseElement(euroCrate)
		);

		return Optional.of(euroCrateConverter.from(newCrateElement));
	}

	public List<EuroCrate> findAllEuroCrates() {
		return euroCrateDatabaseService.findAll(Sort.by("operationCenter", "name").ascending()).stream()
				.map(euroCrateConverter::from)
				.toList();
	}

	public Optional<EuroCrate> findEuroCrate(long id) {
		return euroCrateDatabaseService.findById(id)
				.map(euroCrateConverter::from);
	}

	public Optional<EuroCrateDatabaseElement> findDatabaseElement(long id) {
		return euroCrateDatabaseService.findById(id);
	}

	public int executeUpdate(long id, EuroCratePatchDto euroCratePatchDto) {
		// mega cursed aber glaub der beste weg um eine halbwegs dynamische query zu machen
		// alternativ halt drei mal separate updates aufrufen was wieder extra zeit kostet :agony:

		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaUpdate<EuroCrateDatabaseElement> update = builder.createCriteriaUpdate(EuroCrateDatabaseElement.class);

		// root komponent (update EuroCrateDatabaseElement); die zeile
		Root<EuroCrateDatabaseElement> root = update.from(EuroCrateDatabaseElement.class);

		// where klausel (... where x = y and z = w), properties aus dem root beziehen
		update.where(
				builder.equal(root.get(EuroCrateDatabaseElement_.id), id)
		);

		// individuelle sets für die spalten die wir ändern wollen
		euroCratePatchDto.getDeliveryState().map(deliveryStateConverter::from)
				.ifPresent(it -> update.set(EuroCrateDatabaseElement_.deliveryState, it));
		euroCratePatchDto.getInformation()
				.ifPresent(it -> update.set(EuroCrateDatabaseElement_.information, it));
		euroCratePatchDto.getLocation().map(locationConverter::from).map(locationConverter::toDatabaseElement)
				.ifPresent(it -> update.set(EuroCrateDatabaseElement_.location, it));

		// und los gehts
		return em.createQuery(update).executeUpdate();
	}
}
