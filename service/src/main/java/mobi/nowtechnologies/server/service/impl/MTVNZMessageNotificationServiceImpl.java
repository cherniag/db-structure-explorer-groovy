package mobi.nowtechnologies.server.service.impl;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.service.MessageNotificationService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @author Titov Mykhaylo (titov) on 02.03.2015.
public class MTVNZMessageNotificationServiceImpl implements MessageNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MTVNZMessageNotificationServiceImpl.class);

    private CommunityResourceBundleMessageSource messageSource;

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(User user, String msgCodeBase, String[] msgArgs) {
        Community community = user.getCommunity();

        LOGGER.debug("input parameters user, community, msgCodeBase, msgArgs: [{}], [{}], [{}], [{}]", user, community, msgCodeBase, msgArgs);

        String msgCodeEnding = null;
        if(msgCodeBase.equals("sms.unsubscribe.after.text")){
            msgCodeEnding = getMsgCodeEndingForManualUnsubscriptionVFNZPaymentDetailsCase(user);
        } else if(msgCodeBase.equals("sms.unsubscribe.potential.text")){
            msgCodeEnding = getMsgCodeEndingForNewPaymentDetailsCommittingCase(user);
        }

        String msg = null;
        if(isNotNull(msgCodeEnding)){
            msg = messageSource.getMessage(community.getRewriteUrlParameter(), msgCodeBase + msgCodeEnding, msgArgs, "", null);
        }

        LOGGER.debug("Output parameter msg=[{}]", msg);
        return msg;
    }

    private String getMsgCodeEndingForManualUnsubscriptionVFNZPaymentDetailsCase(User user) {
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        if(currentPaymentDetails.getPaymentType().equals(PaymentDetails.VF_PSMS_TYPE)) {
            if (user.isOnFreeTrial()) {
                return ".for.vfPsms.onFreeTrial.user";
            } else {
                return ".for.vfPsms.onBoughtPeriod.user";
            }
        }
        return null;
    }

    private String getMsgCodeEndingForNewPaymentDetailsCommittingCase(User user) {
        PaymentDetails currentPaymentDetails = user.getCurrentPaymentDetails();
        PaymentDetails previousPaymentDetails = user.getPreviousPaymentDetails();
        if(isNotNull(previousPaymentDetails) && (currentPaymentDetails.getPaymentType().equals(PaymentDetails.VF_PSMS_TYPE) || previousPaymentDetails.getPaymentType().equals(PaymentDetails.VF_PSMS_TYPE))) {
            PaymentPolicy paymentPolicy = currentPaymentDetails.getPaymentPolicy();
            if(previousPaymentDetails.getPaymentPolicy().getId().equals(paymentPolicy.getId())){
                return ".for.vfPsms.user.prevPaymentPolicyIsTheSame";
            }else {
                return ".for.vfPsms.user.prevPaymentPolicyIsDiffer";
            }
        }
        return null;
    }
}
