package de.entropia.logistiktracking.jpa.repo;

import de.entropia.logistiktracking.domain.delivery_state.DeliveryState;
import de.entropia.logistiktracking.domain.operation_center.OperationCenter;
import de.entropia.logistiktracking.jpa.EuroCrateDatabaseElement;
import de.entropia.logistiktracking.jpa.LocationDatabaseElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EuroCrateDatabaseService extends JpaRepository<EuroCrateDatabaseElement, EuroCrateDatabaseElement.EuroCrateDatabaseElementId> {

	@Modifying
	@Query("update EuroCrateDatabaseElement e set e.information = :text where e.operationCenter = :oc and e.name = :name")
	int updateInfoText(@Param("oc") OperationCenter operationCenterDto, @Param("name") String euroCrateName, @Param("text") String infoTextOrNull);

	@Modifying
	@Query("update EuroCrateDatabaseElement e set e.deliveryState = :d where e.operationCenter = :oc and e.name = :name")
	int updateDeliState(@Param("oc") OperationCenter operationCenterDto, @Param("name") String euroCrateName, @Param("d") DeliveryState d);

	@Modifying
	@Query("update EuroCrateDatabaseElement e set e.location = :location where e.operationCenter = :oc and e.name = :name")
	int updateLocation(@Param("oc") OperationCenter operationCenterDto, @Param("name") String euroCrateName, @Param("location") LocationDatabaseElement d);
}
