package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BadgeMappingRepository extends JpaRepository<BadgeMapping, Long> {
    @Query(value="select b from BadgeMapping b where b.community=:community and b.resolution is null order by b.uploaded desc")
    List<BadgeMapping> findAllDefault(@Param("community") Community community);

    @Query(value="select b from BadgeMapping b where b.community=:community and b.resolution=:resolution and b.originalFilenameAlias=:original")
    BadgeMapping findByCommunityResolutionAndOriginalAlias(@Param("community") Community community, @Param("resolution") Resolution resolution, @Param("original") FilenameAlias original);

    @Query(value="select b from BadgeMapping b where b.community=:community and b.originalFilenameAlias=:original")
    List<BadgeMapping> findByCommunityAndOriginalAlias(@Param("community") Community community, @Param("original") FilenameAlias original);

    @Modifying
    @Query(value="delete from BadgeMapping b where b.community=:community and b.originalFilenameAlias=:original")
    void deleteByCommunityAndOriginalAlias(@Param("community") Community community, @Param("original") FilenameAlias original);

    @Query(value="select b from BadgeMapping b where b.resolution=:resolution")
    List<BadgeMapping> findByResolution(@Param("resolution") Resolution resolution);

    @Modifying
    @Query(value="delete from BadgeMapping b where b.resolution=:resolution")
    void deleteByResolution(@Param("resolution") Resolution resolution);

    @Query(value="select b from BadgeMapping b where b.community=:community and b.resolution.deviceType=:deviceType and b.originalFilenameAlias.id=:badgeId order by b.resolution.deviceType, b.resolution.width, b.resolution.height desc")
    List<BadgeMapping> findByCommunityAndDeviceType(@Param("community") Community community, @Param("deviceType") String deviceType, @Param("badgeId") long badgeId);
}
