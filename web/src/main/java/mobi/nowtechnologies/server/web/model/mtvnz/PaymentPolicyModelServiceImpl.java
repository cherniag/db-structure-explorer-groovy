package mobi.nowtechnologies.server.web.model.mtvnz;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.web.model.PaymentPolicyModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentPolicyModelServiceImpl  implements PaymentPolicyModelService {
    Logger logger = LoggerFactory.getLogger(getClass());

    UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Map<String, Object> getModel(User user) {
        Map<String, Object> model = new HashMap<>();

        boolean vfPaymentType = hasVodafonePaymentType(user);

        if(vfPaymentType) {
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
        User user = userService.getWithSocial(u.getId());
        List<SocialInfo> socialInfo = new ArrayList<>(user.getSocialInfo());

        //to get predictable socialInfo from set
        Collections.sort(socialInfo, new Comparator<SocialInfo>() {
            @Override
            public int compare(SocialInfo o1, SocialInfo o2) {
                return o2.getSocialId().compareTo(o1.getSocialId());
            }
        });

        SocialInfo first = socialInfo.iterator().next();
        return StringUtils.substring(first.getFirstName(), 0, 15) + "...";
    }
}
