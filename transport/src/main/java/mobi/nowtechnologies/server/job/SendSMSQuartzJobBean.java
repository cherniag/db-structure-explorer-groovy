package mobi.nowtechnologies.server.job;

import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.PaymentDetailsService;
import mobi.nowtechnologies.server.service.UserNotificationService;
import mobi.nowtechnologies.server.shared.log.LogUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

public class SendSMSQuartzJobBean extends QuartzJobBean implements StatefulJob{

    private static final Logger LOGGER = LoggerFactory.getLogger(SendSMSQuartzJobBean.class);

    private String communityUrl;
    private int paymentDetailsFetchSize;
    private PaymentDetailsService paymentDetailsService;
    private UserNotificationService userNotificationService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        process(context);
    }

    private void process(JobExecutionContext context) {
        try{
            LogUtils.putClassNameMDC(this.getClass());
            init(context.getMergedJobDataMap());
            LOGGER.info("[START] Send SMS job started for [{}] community users", communityUrl);
            execute();
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }finally {
            LOGGER.info("[START] Send SMS job finished for [{}] community users", communityUrl);
            LogUtils.removeGlobalMDC();
        }
    }

    private void init(JobDataMap jobDataMap) {
        paymentDetailsService = (PaymentDetailsService) jobDataMap.get("paymentDetailsService");
        communityUrl = (String) jobDataMap.get("communityURL");
        paymentDetailsFetchSize = Integer.parseInt((String)jobDataMap.get("paymentDetailsFetchSize"));
        userNotificationService = (UserNotificationService) jobDataMap.get("userNotificationService");
    }

    private void execute() {
        LOGGER.info("Attempt to fetch [{}] failed payment with no notification payment details", paymentDetailsFetchSize);
        List<PaymentDetails> paymentDetails = paymentDetailsService.findFailedPaymentWithNoNotificationPaymentDetails(communityUrl, new PageRequest(0, paymentDetailsFetchSize));
        LOGGER.info("Fetched [{}] failed payment with no notification payment details", paymentDetails.size());

        for (PaymentDetails paymentDetail : paymentDetails) {
            try {
                 userNotificationService.sendPaymentFailSMS(paymentDetail, 0);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

}
