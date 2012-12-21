package mobi.nowtechnologies.server.transport.controller;

import mobi.nowtechnologies.server.persistence.domain.Promotion;
import mobi.nowtechnologies.server.persistence.domain.Response;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.service.O2ClientService;
import mobi.nowtechnologies.server.service.UserService;
import mobi.nowtechnologies.server.shared.dto.AccountCheckDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * ApplyInitPromoConroller
 *
 * @author Titov Mykhaylo (titov)
 * @author Alexander Kollpakov (akolpakov)
 */
@Controller
public class ApplyInitPromoController extends CommonController {

    private UserService userService;
    private O2ClientService o2ClientService;

    public void setO2ClientService(O2ClientService o2ClientService) {
        this.o2ClientService = o2ClientService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}}/APPLY_INIT_PROMO", "/{apiVersion:[3-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}}/APPLY_INIT_PROMO"})
    public ModelAndView applyInitialPromotion(
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp) {

        LOGGER.info("command processing started");
        try {
            User user = userService.findByNameAndCommunity(userName, communityName);

            AccountCheckDTO accountCheckDTO = userService.applyInitialPromotion(user);
            final Object[] objects = new Object[]{accountCheckDTO};
            proccessRememberMeToken(objects);

            return new ModelAndView(view, Response.class.toString(), new Response(objects));
        } finally {
            LOGGER.info("command processing finished");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = {"/O2/3.6/APPLY_INIT_PROMO", "*/O2/3.6/APPLY_INIT_PROMO"})
    public void applyO2Promotion(
            @RequestParam("COMMUNITY_NAME") String communityName,
            @RequestParam("USER_NAME") String userName,
            @RequestParam("USER_TOKEN") String userToken,
            @RequestParam("TIMESTAMP") String timestamp,
            @RequestParam("OTAC_TOKEN") String token) {

        User user = userService.findByNameAndCommunity(userName, communityName);
        Promotion promotion = null;
        if(o2ClientService.isO2User(o2ClientService.getUserDetails(token)))
            promotion = userService.setPotentialPromo(communityName, user, "promotionCode");
        else
            promotion = userService.setPotentialPromo(communityName, user, "defaultPromotionCode");

        userService.applyPromotionByPromoCode(user, promotion);
    }

}
