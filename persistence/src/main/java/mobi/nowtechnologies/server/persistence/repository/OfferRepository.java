package mobi.nowtechnologies.server.persistence.repository;

import java.util.List;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Offer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kolpakov (akolpakov)
 * 
 */
public interface OfferRepository extends JpaRepository<Offer, Integer> {
	
	@Query("select COUNT(distinct offer) from Offer offer where offer.community=?")
	long countWithFiltersByCommunity(Community community);
	
	@Query("select distinct offer from Offer offer left join FETCH offer.filterWithCtiteria where offer.community=? order by offer.title asc")
	List<Offer> findWithFiltersByCommunity(Community community);
	
	@Query("select distinct offer from Offer offer left join FETCH offer.items left join FETCH offer.filterWithCtiteria where offer.community=? order by offer.title asc")
	List<Offer> findWithItemsAndFiltersByCommunity(Community community);

	@Query("select distinct offer from Offer offer left join FETCH offer.filterWithCtiteria where offer.id=?")
	Offer findOneWithFilters(Integer offerId);
}