package mobi.nowtechnologies.server.service.pincode.impl;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.pincode.PinCodeSmsService;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.service.sms.SMSResponse;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Anton Zemliankin
 */

public class PinCodeSmsServiceImpl implements PinCodeSmsService {

    private static final String DEFAULT_SMS_PROVIDER_KEY = "DEFAULT";

    @Resource
    Map<String, SMSGatewayService> smsProviders;

    CommunityResourceBundleMessageSource messageSource;

    @Override
    public boolean sendPinCode(User user, String msisdn, String pinCode) {
        String community = user.getUserGroup().getCommunity().getRewriteUrlParameter();

        String message = messageSource.getMessage(community, "pin.code.sms.confirm.message", new Object[]{pinCode}, null);
        String title = messageSource.getMessage(community, "pin.code.sms.confirm.title", null, null);

        SMSResponse smsResponse = getSMSProvider(community).send(msisdn, message, title);
        return smsResponse.isSuccessful();
    }

    private SMSGatewayService getSMSProvider(String community) {
        if (smsProviders.containsKey(community)) {
            return smsProviders.get(community);
        } else {
            return smsProviders.get(DEFAULT_SMS_PROVIDER_KEY);
        }
    }

    public void setMessageSource(CommunityResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
