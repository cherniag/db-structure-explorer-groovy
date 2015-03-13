package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.NotPromotedDevice;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotPromotedDeviceRepository extends JpaRepository<NotPromotedDevice, String> {

    NotPromotedDevice findByDeviceUIDAndCommunity(String deviceUID, Community community);
}