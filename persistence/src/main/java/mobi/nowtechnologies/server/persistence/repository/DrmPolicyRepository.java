package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.DrmPolicy;

import org.springframework.data.jpa.repository.JpaRepository;

// @author Titov Mykhaylo (titov) on 20.11.2014.
public interface DrmPolicyRepository extends JpaRepository<DrmPolicy, Byte> {}
