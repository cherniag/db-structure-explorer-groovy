package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.PinCode;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.exception.PinCodeException;
import mobi.nowtechnologies.server.service.exception.SubscriberServiceException;
import mobi.nowtechnologies.server.service.impl.SmsServiceFacade;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.pincode.PinCodeService;
import mobi.nowtechnologies.server.service.sms.SMSGatewayService;
import mobi.nowtechnologies.server.web.model.EnterPhoneModelService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

class EnterPhoneModelServiceImpl implements EnterPhoneModelService {
    @Resource
    PinCodeService pinCodeService;
    @Resource
    NZSubscriberInfoService nzSubscriberInfoService;
    @Resource
    SmsServiceFacade smsServiceFacade;

    @Override
    public Map<String, Object> getModel(User user, String phone) {
        Map<String, Object> model = new HashMap<>();

        CheckResult checkResult = doCheck(phone);

        if(checkResult.isYes()){
            try {
                PinCode generated = pinCodeService.generate(user, 4);

                SMSGatewayService smsProvider = smsServiceFacade.getSMSProvider(user.getCommunityRewriteUrl());

                String code = generated.getCode();

                smsProvider.send(phone, "Pin " + code, "The title");

                model.put("result", checkResult);
            } catch (PinCodeException.MaxPinCodesReached maxPinCodesReached) {
                model.put("result", CheckResult.LIMIT_REACHED);
            }
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
        } catch (SubscriberServiceException.ServiceNotAvailable e) {
            return CheckResult.CONN_ERROR;
        } catch (SubscriberServiceException.MSISDNNotFound e) {
            return CheckResult.NO;
        }
    }

    public static enum CheckResult {
        YES, NO, CONN_ERROR, LIMIT_REACHED;

        public boolean isYes() {
            return this == YES;
        }

        public boolean isConnectionProblem() {
            return this == CONN_ERROR;
        }
    }
}
