package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.PromoCode;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User: Titov Mykhaylo (titov) 02.08.13 15:36
 */
public interface PromoCodeRepository extends JpaRepository<PromoCode, Integer> {

}
