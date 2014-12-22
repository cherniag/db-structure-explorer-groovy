package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.assembler.ReferralAsm;
import mobi.nowtechnologies.server.security.bind.annotation.AuthenticatedUser;
import mobi.nowtechnologies.server.dto.ReferralDto;
import mobi.nowtechnologies.server.persistence.domain.referral.Referral;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.ReferralService;
import mobi.nowtechnologies.server.shared.enums.ActivationStatus;
import mobi.nowtechnologies.server.transport.controller.core.CommonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Author: Gennadii Cherniaiev
 * Date: 11/21/2014
 */
@Controller
public class ReferralController extends CommonController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ReferralService referralService;

    @Resource
    private ReferralAsm referralAsm;

    @RequestMapping(method = POST,
            value = {
                    "**/{community}/{apiVersion:6.7}/REFERRALS"
            })
    @ResponseStatus(value = HttpStatus.OK)
    public void saveReferrals(@AuthenticatedUser User user,
                              @RequestBody List<ReferralDto> referralDtos) throws Exception {
        logger.info("REFERRALS started: userName [{}], referralDtos: [{}]", user.getUserName(), referralDtos);
        userService.authorize(user, false, ActivationStatus.ACTIVATED);
        List<Referral> converted = referralAsm.fromDtos(referralDtos, user);
        referralService.refer(converted);
        logger.info("REFERRALS finished");
    }

}
