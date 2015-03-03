package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.versioncheck.VersionMessage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VersionMessageRepository extends JpaRepository<VersionMessage, Long> {}
