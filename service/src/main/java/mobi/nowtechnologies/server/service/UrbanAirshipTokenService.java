package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.UrbanAirshipToken;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UrbanAirshipTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by enes on 1/27/15.
 */
public class UrbanAirshipTokenService {

    private final static Logger logger = LoggerFactory.getLogger(UrbanAirshipToken.class);

    @Resource
    private UrbanAirshipTokenRepository urbanAirshipTokenRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public UrbanAirshipToken saveToken(User user, String token) {
        logger.info("Saving token for user with id: {}, token: {}", user.getId(), token);

        UrbanAirshipToken urbanAirshipToken = urbanAirshipTokenRepository.findDataByUserId(user.getId());

        if (urbanAirshipToken == null) {
            logger.info("Token does not exist, creating a new one...");
            urbanAirshipToken = new UrbanAirshipToken();
            urbanAirshipToken.setUser(user);
            urbanAirshipToken.setToken(token);
            urbanAirshipTokenRepository.save(urbanAirshipToken);
        } else if (!token.equals(urbanAirshipToken.getToken())) {
            logger.info("Token differs from already persisted one, updating...");
            urbanAirshipToken.setToken(token);
            urbanAirshipTokenRepository.save(urbanAirshipToken);
        }

        return urbanAirshipToken;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void mergeToken(User fromUser, User toUser) {
        UrbanAirshipToken fromToken = urbanAirshipTokenRepository.findDataByUserId(fromUser.getId());
        UrbanAirshipToken toToken = urbanAirshipTokenRepository.findDataByUserId(toUser.getId());

        if (fromToken == null && toToken == null) {
            return;
        }

        if (fromToken != null) {
            urbanAirshipTokenRepository.delete(fromToken);
            urbanAirshipTokenRepository.flush();

            if (toToken == null) {
                toToken = new UrbanAirshipToken();
                toToken.setUser(toUser);
                toToken.setToken(fromToken.getToken());
            } else {
                toToken.setToken(fromToken.getToken());
            }

            urbanAirshipTokenRepository.save(toToken);
        }
    }
}
