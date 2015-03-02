package mobi.nowtechnologies.server.web.controller;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentDetails;
import mobi.nowtechnologies.server.persistence.domain.payment.PaymentPolicy;
import mobi.nowtechnologies.server.persistence.domain.social.SocialInfo;
import mobi.nowtechnologies.server.persistence.repository.PaymentPolicyRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.service.payment.PSMSPaymentService;
import mobi.nowtechnologies.server.shared.enums.PaymentDetailsStatus;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;
import mobi.nowtechnologies.server.web.model.CommunityServiceFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class SmsPaymentController extends CommonController {
    @Resource
    UserRepository userRepository;
    @Resource
    PaymentPolicyRepository paymentPolicyRepository;
    @Resource
    CommunityServiceFactory communityServiceFactory;
    @Resource
    CommunityResourceBundleMessageSource communityResourceBundleMessageSource;
    UserService userService;

    @RequestMapping(value = {"smspayment/result"}, method = RequestMethod.GET)
    public ModelAndView commit(@RequestParam("id") int policyId) {
        ModelAndView modelAndView = new ModelAndView("smspayment/result");

        User user = getUser();

        if(hasAwaitingStatus(user)) {
            modelAndView.addObject("awaiting", true);
            return modelAndView;
        }

        if(samePolicy(user, policyId)) {
            return modelAndView;
        }

        boolean vfPaymentType = hasVodafonePaymentType(user);

        assign(policyId, user);

        if(vfPaymentType) {
            modelAndView.addObject("changed", true);
        } else {
            modelAndView.addObject("customerName", getSocialName(user));
        }

        return modelAndView;
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

    private boolean samePolicy(User user, int policyId) {
        return user.getCurrentPaymentDetails() != null &&
                user.getCurrentPaymentDetails().isActivated() &&
                user.getCurrentPaymentDetails().getPaymentPolicy() != null &&
                user.getCurrentPaymentDetails().getPaymentPolicy().getId() == policyId;
    }

    private boolean hasVodafonePaymentType(User user) {
        return user.getCurrentPaymentDetails() != null &&
                PaymentDetails.VF_PSMS_TYPE.equals(user.getCurrentPaymentDetails().getPaymentType()) &&
                user.getCurrentPaymentDetails().isActivated();
    }

    private boolean hasAwaitingStatus(User user) {
        return user.getCurrentPaymentDetails() != null && user.getCurrentPaymentDetails().getLastPaymentStatus() == PaymentDetailsStatus.AWAITING;
    }

    private void assign(int policyId, User user) {
        PaymentPolicy policy = paymentPolicyRepository.findOne(policyId);
        PSMSPaymentService psmsPaymentService = communityServiceFactory.find(user.getCommunity(), PSMSPaymentService.class);
        psmsPaymentService.commitPaymentDetails(user, policy);
    }

    private User getUser() {
        return userRepository.findOne(getUserId());
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
