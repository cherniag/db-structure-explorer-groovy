package mobi.nowtechnologies.server.trackrepo.repository;

import mobi.nowtechnologies.server.trackrepo.domain.AssetFile;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 
 * @author Alexander Kolpakov (akolpakov)
 *
 */
public interface FileRepository extends JpaRepository<AssetFile, Long> {
}
