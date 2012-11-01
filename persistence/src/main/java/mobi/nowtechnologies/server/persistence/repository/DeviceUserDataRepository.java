package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeviceUserDataRepository extends JpaRepository<DeviceUserData, Integer> {

	@Query("select data from DeviceUserData data where data.xtifyToken = ?1")
	DeviceUserData findByXtifyToken(String token);

}
