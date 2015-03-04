package mobi.nowtechnologies.server.trackrepo.repository;

import mobi.nowtechnologies.server.trackrepo.domain.IngestionLog;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Alexander Kolpakov (akolpakov)
 */
public interface IngestionLogRepository extends JpaRepository<IngestionLog, Long> {}
