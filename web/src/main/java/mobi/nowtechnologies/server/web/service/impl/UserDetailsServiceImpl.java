package mobi.nowtechnologies.server.web.service.impl;


import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import mobi.nowtechnologies.server.shared.web.utils.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        LOGGER.debug("input parameters userName: [{}]", userName);

        String communityUrl = RequestUtils.getCommunityURL();
        User user = userRepository.findByUserNameAndCommunityUrl(userName, communityUrl);

        if (user == null) {
            throw new UsernameNotFoundException("Couldn't find user with username [" + userName + "] and communityUrl [" + communityUrl + "] in the DB");
        }

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl(user, false);

        LOGGER.debug("Output parameter userDetailsImpl=[{}]", userDetailsImpl);
        return userDetailsImpl;
    }

}

