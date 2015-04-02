package mobi.nowtechnologies.server.web.service.impl;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.impl.SmsServiceFacade;
import mobi.nowtechnologies.server.service.pincode.MaxGenerationReachedException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

public class PinService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    PinCodeService pinCodeService;
    @Resource
    SmsServiceFacade smsServiceFacade;
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;

    public void sendPinToUser(User user, String phone) throws MaxGenerationReachedException {
        PinCode generated = pinCodeService.generate(user, 4);

        SMSGatewayService smsProvider = smsServiceFacade.getSMSProvider(user.getCommunityRewriteUrl());

        String code = generated.getCode();

        String smsText = communityResourceBundleMessageSource.getMessage(user.getCommunityRewriteUrl(), "enter.phone.pin.sms.text", new Object[]{code}, null);
        String smsTitle = communityResourceBundleMessageSource.getMessage(user.getCommunityRewriteUrl(), "sms.title", null, null);

        smsProvider.send(phone, smsText, smsTitle);

        logger.info("Sms was sent to user id {}", user.getId());
    }
}
