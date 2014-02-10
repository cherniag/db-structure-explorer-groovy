package mobi.nowtechnologies.server.persistence.repository;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivationEmailRepository extends JpaRepository<ActivationEmail, Long> {
}
