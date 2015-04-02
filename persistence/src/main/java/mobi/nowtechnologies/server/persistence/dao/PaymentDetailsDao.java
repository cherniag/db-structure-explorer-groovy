package mobi.nowtechnologies.server.persistence.dao;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.Operator;
import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.payment.PromotionPaymentPolicy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.orm.jpa.support.JpaDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PaymentDetailsDao extends JpaDaoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentDetailsDao.class.getName());

    private EntityDao entityDao;

    public void setEntityDao(EntityDao entityDao) {
        this.entityDao = entityDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentDetails update(PaymentDetails paymentDetails) throws DataAccessException {
        return getJpaTemplate().merge(paymentDetails);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public PromotionPaymentPolicy getPromotionPaymentPolicy(Promotion promotion, PaymentPolicy paymentPolicy) {
        List<PromotionPaymentPolicy> promotionPayments = getJpaTemplate().findByNamedQuery(PromotionPaymentPolicy.NQ_GET_PROMOTION_PAYMENT_WITH_PAYMENT_POLICY, promotion, paymentPolicy);
        return (null != promotionPayments && promotionPayments.size() > 0) ?
               promotionPayments.get(0) :
               null;
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<Operator> getAvailableOperators(Community community, String paymentType) {
        List<Operator> result = getJpaTemplate().findByNamedQuery(PaymentPolicy.GET_OPERATORS_LIST, (int) community.getId(), paymentType);
        return (null != result && result.size() > 0) ?
               result :
               null;
    }
}
