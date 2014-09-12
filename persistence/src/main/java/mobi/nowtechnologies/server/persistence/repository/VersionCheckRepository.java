package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
public interface VersionCheckRepository extends JpaRepository<VersionCheck, Long> {
    List<VersionCheck> findByCommunityAndDeviceType(Community community, DeviceType platform);

    @Query("select")
    VersionCheck findSuitableVersion(int majorNumber, int minorNumber, int revisionNumber);
}
