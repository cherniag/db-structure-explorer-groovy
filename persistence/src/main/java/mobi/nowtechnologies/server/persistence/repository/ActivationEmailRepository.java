package mobi.nowtechnologies.server.persistence.repository;


import mobi.nowtechnologies.server.persistence.domain.ActivationEmail;
import mobi.nowtechnologies.server.persistence.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivationEmailRepository extends JpaRepository<ActivationEmail, Long> {
    ActivationEmail findByUserAndEmail(User user, String email);
}
