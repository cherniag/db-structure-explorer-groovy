package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.payment.AbstractPayment;
import mobi.nowtechnologies.server.persistence.domain.payment.SubmittedPayment;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.support.JpaDaoSupport;

/**
 * @author Titov Mykhaylo (titov)
 */
@Deprecated
public class PaymentDao extends JpaDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDao.class.getName());


    @SuppressWarnings("unchecked")
    public List<AbstractPayment> findByUserIdOrderedByTimestampDesc(final int userId, final Integer maxResults) {
        LOGGER.debug("input parameters userId: [{}], [{}]", userId, maxResults);

        List<AbstractPayment> abstractPayments = getJpaTemplate().executeFind(new JpaCallback<List>() {
            @Override
            public List<AbstractPayment> doInJpa(EntityManager entityManager) throws javax.persistence.PersistenceException {
                Query query = entityManager.createNamedQuery(SubmittedPayment.NQ_FIND_BY_USER_ID_ORDERED_BY_TIMESTAMP_DESC);
                query.setParameter(1, userId);
                List<AbstractPayment> abstractPayments = query.getResultList();
                if (maxResults != null && abstractPayments.size() > maxResults) {
                    abstractPayments = abstractPayments.subList(0, maxResults);
                }
                return abstractPayments;
            }
        });

        LOGGER.debug("Output parameter abstractPayments=[{}]", abstractPayments);
        return abstractPayments;
    }
}
