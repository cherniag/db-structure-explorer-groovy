package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.UserDeviceDetails;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * User: Titov Mykhaylo (titov) 14.10.13 17:47
 */
@NoRepositoryBean
public interface UserDeviceDetailsRepository<T extends UserDeviceDetails> extends PagingAndSortingRepository<T, Integer> {

}
