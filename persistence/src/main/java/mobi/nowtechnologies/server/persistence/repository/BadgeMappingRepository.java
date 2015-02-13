package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.FilenameAlias;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.BadgeMapping;
import mobi.nowtechnologies.server.persistence.domain.streamzine.badge.Resolution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BadgeMappingRepository extends JpaRepository<BadgeMapping, Long> {
    @Query(value="select b from BadgeMapping b where b.community=:community and b.resolution is null and b.hidden is null order by b.uploaded desc")
    List<BadgeMapping> findAllDefault(@Param("community") Community community);

    @Query(value="select b from BadgeMapping b where b.resolution is null and b.hidden is null")
    List<BadgeMapping> findAllDefault();

    @Query(value="select b from BadgeMapping b where b.community=:community and b.resolution=:resolution and b.originalFilenameAlias=:original")
    BadgeMapping findByCommunityResolutionAndOriginalAlias(@Param("community") Community community, @Param("resolution") Resolution resolution, @Param("original") FilenameAlias original);

    @Query(value="select b from BadgeMapping b where b.community=:community and b.originalFilenameAlias=:original and b.resolution in (:resolutions)")
    List<BadgeMapping> findByCommunityResolutionsAndOriginalAlias(@Param("community") Community community, @Param("original") FilenameAlias original, @Param("resolutions") Collection<Resolution> resolutions);

    @Query(value="select b from BadgeMapping b where b.community=:community and b.originalFilenameAlias=:original")
    List<BadgeMapping> findByCommunityAndOriginalAlias(@Param("community") Community community, @Param("original") FilenameAlias original);

    @Query(value="select b from BadgeMapping b where b.resolution=:resolution")
    List<BadgeMapping> findByResolution(@Param("resolution") Resolution resolution);

    @Modifying
    @Query(value="delete from BadgeMapping b where b.resolution=:resolution")
    void deleteByResolution(@Param("resolution") Resolution resolution);

    @Query(value="select b from BadgeMapping b where b.community=:community and b.originalFilenameAlias.id=:badgeId and (b.resolution=:resolution or b.resolution is null) order by b.resolution desc")
    List<BadgeMapping> findByCommunityResolutionAndFilenameId(@Param("community") Community community, @Param("resolution") Resolution resolution, @Param("badgeId") long badgeId);

    @Query(value="select b from BadgeMapping b where b.community=:community and b.originalFilenameAlias.id=:badgeId and b.resolution is null order by b.resolution desc")
    List<BadgeMapping> findByCommunityAndFilenameId(@Param("community") Community community, @Param("badgeId") long badgeId);
}
