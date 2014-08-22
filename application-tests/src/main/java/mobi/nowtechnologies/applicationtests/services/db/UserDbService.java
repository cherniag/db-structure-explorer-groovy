package mobi.nowtechnologies.applicationtests.services.db;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserDbService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private CommunityRepository communityRepository;

    public User getUserByDeviceUIDAndCommunity(String deviceUID, String communityUrl) {
        Community community = communityRepository.findByRewriteUrlParameter(communityUrl);
        return userRepository.findByDeviceUIDAndCommunity(deviceUID, community);
    }

    public User getUserByUserNameAndCommunity(String userName, String communityUrl) {
        return userRepository.findOne(userName, communityUrl);
    }
}
