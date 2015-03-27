package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.shared.Utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Titov Mykhaylo (titov)
 */
public class PromotionDao extends JpaDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(PromotionDao.class);

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Promotion> getPromotionWithFilters(final int userGroupId) {
        LOGGER.debug("input parameters userGroupId: [{}]", userGroupId);
        int currentTime = Utils.getEpochSeconds();
        final List<Promotion> promotionWithFilters = getJpaTemplate().findByNamedQuery(Promotion.NQ_GET_PROMOTION_WITH_FILTER, currentTime, userGroupId);
        LOGGER.info("Output parameter promotionWithFilters=[{}]", promotionWithFilters);
        return promotionWithFilters;
    }
}