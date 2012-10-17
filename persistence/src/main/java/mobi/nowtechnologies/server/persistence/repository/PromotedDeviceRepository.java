package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.PromotedDevice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotedDeviceRepository extends JpaRepository<PromotedDevice, String> {

	PromotedDevice findByDeviceUIDAndCommunity(String deviceUID, Community community);
}