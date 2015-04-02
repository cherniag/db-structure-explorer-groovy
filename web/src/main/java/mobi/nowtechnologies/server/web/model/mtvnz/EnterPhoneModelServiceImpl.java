package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.nz.MsisdnNotFoundException;
import mobi.nowtechnologies.server.service.nz.NZSubscriberInfoService;
import mobi.nowtechnologies.server.service.nz.ProviderNotAvailableException;
import mobi.nowtechnologies.server.service.pincode.MaxGenerationReachedException;
import mobi.nowtechnologies.server.web.model.EnterPhoneModelService;
import mobi.nowtechnologies.server.web.service.impl.PinService;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EnterPhoneModelServiceImpl implements EnterPhoneModelService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    NZSubscriberInfoService nzSubscriberInfoService;
    @Resource
    PaymentModelServiceImpl paymentModelService;
    @Resource
    PinService pinService;

    @Override
    public Map<String, Object> getModel(User user, String phone) {
        Map<String, Object> model = new HashMap<>();

        CheckResult checkResult = doCheck(phone);

        model.put("result", checkResult);

        logger.info("Result of check {}", checkResult);

        if(checkResult.isYes()) {
            try {
                pinService.sendPinToUser(user, phone);
            } catch (MaxGenerationReachedException maxGenerationReached) {
                model.put("result", CheckResult.LIMIT_REACHED);
                model.put("check", false);

                logger.warn("max pin generation reached for user {}", user.getId());
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
