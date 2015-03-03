package mobi.nowtechnologies.applicationtests.services.db;

import mobi.nowtechnologies.applicationtests.services.device.PhoneState;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class UserDbService {

    @Resource
    private UserRepository userRepository;
    @Resource
    private CommunityRepository communityRepository;

    public User findUser(PhoneState phoneState, UserDeviceData data) {
        Community community = communityRepository.findByRewriteUrlParameter(data.getCommunityUrl());
        return userRepository.findByDeviceUIDAndCommunity(phoneState.getDeviceUID(), community);
    }

    public User getUserByUserNameAndCommunity(String userName, String communityUrl) {
        return userRepository.findOne(userName, communityUrl);
    }
}
