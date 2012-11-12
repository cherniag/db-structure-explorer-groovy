package mobi.nowtechnologies.server.track_repo.repository;

import mobi.nowtechnologies.server.track_repo.domain.AssetFile;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public interface FileRepository extends JpaRepository<AssetFile, Long> {
}
