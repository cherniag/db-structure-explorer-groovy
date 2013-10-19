package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class SendSMSQuartzJobBean extends QuartzJobBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendSMSQuartzJobBean.class);

    private String communityUrl;
    private int paymentDetailsFetchSize;
    private PaymentDetailsService paymentDetailsService;

    public void setCommunityUrl(String communityUrl) {
        this.communityUrl = communityUrl;
    }

    public void setPaymentDetailsFetchSize(int paymentDetailsFetchSize) {
        this.paymentDetailsFetchSize = paymentDetailsFetchSize;
    }

    public PaymentDetailsService getPaymentDetailsService() {
        return paymentDetailsService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        process();
    }

    private void process() {
        try{
            LogUtils.putClassNameMDC(this.getClass());
            LOGGER.info("[START] Send SMS job started for [{}] community users", communityUrl);
            ex();
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }finally {
            LOGGER.info("[START] Send SMS job finished for [{}] community users", communityUrl);
            LogUtils.removeGlobalMDC();
        }
    }

    private void ex() {
        List<PaymentDetails> paymentDetails = paymentDetailsService.findFailurePaymentPaymentDetailsWithNoNotification(communityUrl, new PageRequest(0, paymentDetailsFetchSize));
    }

}
