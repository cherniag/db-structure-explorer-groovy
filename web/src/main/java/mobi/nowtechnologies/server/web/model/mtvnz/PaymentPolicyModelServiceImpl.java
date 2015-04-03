package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfo;
import mobi.nowtechnologies.server.social.domain.SocialNetworkInfoRepository;
import mobi.nowtechnologies.server.web.model.PaymentPolicyModelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentPolicyModelServiceImpl implements PaymentPolicyModelService {

    Logger logger = LoggerFactory.getLogger(getClass());

    UserService userService;

    SocialNetworkInfoRepository socialNetworkInfoRepository;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setSocialNetworkInfoRepository(SocialNetworkInfoRepository socialNetworkInfoRepository) {
        this.socialNetworkInfoRepository = socialNetworkInfoRepository;
    }

    @Override
    public Map<String, Object> getModel(User user) {
        Map<String, Object> model = new HashMap<>();

        boolean vfPaymentType = hasVodafonePaymentType(user);

        if (vfPaymentType) {
            model.put("changed", true);
        } else {
            model.put("customerName", getSocialName(user));
        }

        logger.info("Model for user id {}: {}", user.getId(), model);

        return model;
    }

    private boolean hasVodafonePaymentType(User user) {
        return user.getCurrentPaymentDetails() != null &&
               PaymentDetails.MTVNZ_PSMS_TYPE.equals(user.getCurrentPaymentDetails().getPaymentType()) &&
               user.getCurrentPaymentDetails().isActivated();
    }

    private String getSocialName(User u) {
        List<SocialNetworkInfo> socialNetworkInfo = socialNetworkInfoRepository.findByUserId(u.getId());

        SocialNetworkInfo first = socialNetworkInfo.iterator().next();
        return Utils.truncateToLengthWithEnding(first.getFirstName(), 15, "...");
    }
}
