/*
 * Copyright 2015 Musicqubed.com. All Rights Reserved.
 */

package mobi.nowtechnologies.server.service.payment;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.service.exception.ServiceException;
import mobi.nowtechnologies.server.service.payment.http.MigHttpService;
import mobi.nowtechnologies.server.service.payment.response.MigResponse;
import mobi.nowtechnologies.server.shared.Utils;
import mobi.nowtechnologies.server.shared.message.CommunityResourceBundleMessageSource;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional;

public class PinMigService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    MigHttpService httpService;

    @Resource(name = "serviceMessageSource")
    CommunityResourceBundleMessageSource messageSource;
    @Resource
    UserRepository userRepository;

    @Transactional
    public void sendPin(int userId, String phoneNumber) {
        final String pin = Utils.generateRandom4DigitsPIN();

        User user = userRepository.findOne(userId);
        String communityUrl = user.getUserGroup().getCommunity().getRewriteUrlParameter().toLowerCase();
        String message = messageSource.getMessage(communityUrl, "sms.freeMsg", new Object[] {pin}, null);
        MigResponse response = httpService.makeFreeSMSRequest(phoneNumber, message);

        if (response.isSuccessful()) {
            logger.info("Free sms with pin code:{} was sent for user:{}", pin, userId);
            user.setPin(pin);
            userRepository.save(user);
        } else {
            String errorMessage = String.format("Pin %s was not sent for user %s and phone number %s due to %s", pin, user.getId(), phoneNumber, response.getDescriptionError());
            logger.error(errorMessage);
            throw new ServiceException(errorMessage);
        }
    }
}
