package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.impl.SmsServiceFacade;
import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.ProviderNotAvailableException;
import mobi.nowtechnologies.server.service.pincode.MaxGenerationReachedException;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.web.model.EnterPhoneModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

class EnterPhoneModelServiceImpl implements EnterPhoneModelService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    PinCodeService pinCodeService;
    @Resource
    NZSubscriberInfoService nzSubscriberInfoService;
    @Resource
    SmsServiceFacade smsServiceFacade;
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;
    @Resource
    PaymentModelServiceImpl paymentModelService;

    @Override
    public Map<String, Object> getModel(User user, String phone) {
        Map<String, Object> model = new HashMap<>();

        CheckResult checkResult = doCheck(phone);

        model.put("result", checkResult);

        logger.info("Result of check {}", checkResult);

        if(checkResult.isYes()){
            try {
                PinCode generated = pinCodeService.generate(user, 4);

                logger.info("Generated Pin Code {}", generated);

                SMSGatewayService smsProvider = smsServiceFacade.getSMSProvider(user.getCommunityRewriteUrl());

                String code = generated.getCode();

                String smsText = communityResourceBundleMessageSource.getMessage(user.getCommunityRewriteUrl(), "enter.phone.pin.sms.text", new Object[]{code}, null);
                String smsTitle = communityResourceBundleMessageSource.getMessage(user.getCommunityRewriteUrl(), "sms.title", null, null);

                smsProvider.send(phone, smsText, smsTitle);

                logger.info("Sms was sent to user id {}", user.getId());
            } catch (MaxGenerationReachedException maxGenerationReached) {
                model.put("result", CheckResult.LIMIT_REACHED);
                model.put("check", false);
            }
        }

        if(checkResult.isNo()) {
            model.putAll(paymentModelService.getModel(user));
        }

        return model;
    }

    private CheckResult doCheck(String phone) {
        try {
            if(nzSubscriberInfoService.belongs(phone)){
                return CheckResult.YES;
            } else {
                return CheckResult.NO;
            }
        } catch (ProviderNotAvailableException e) {
            return CheckResult.CONN_ERROR;
        } catch (MsisdnNotFoundException e) {
            return CheckResult.NOT_VALID;
        }
    }

    public static enum CheckResult {
        YES, NO, CONN_ERROR, NOT_VALID, LIMIT_REACHED;

        public boolean isYes() {
            return this == YES;
        }

        public boolean isNo(){
            return this == NO;
        }

        public boolean isConnectionError() {
            return this == CONN_ERROR;
        }

        public boolean isLimitReached() {
            return this == LIMIT_REACHED;
        }

        public boolean isNotValid() {
            return this == NOT_VALID;
        }

    }
}
