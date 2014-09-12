package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceType;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionCheck;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Oleg Artomov on 9/11/2014.
 */
public interface VersionCheckRepository extends JpaRepository<VersionCheck, Long> {
     @Query(value="select entity from #{#entityName} entity where " +
            "(entity.community=?1 and entity.deviceType=?2 and entity.applicationName=?3) and " +
            "(entity.majorNumber>?4 or " +
            "(entity.majorNumber = ?4 and entity.minorNumber>?5) or " +
            "(entity.majorNumber = ?4 and entity.minorNumber = ?5 and entity.revisionNumber>?6) or " +
            "(entity.majorNumber = ?4 and entity.minorNumber = ?5 and entity.revisionNumber=?6))")
    List<VersionCheck> findSuitableVersions(Community community, DeviceType platform, String applicationName, int majorNumber, int minorNumber, int revisionNumber, Pageable pageable);
}
