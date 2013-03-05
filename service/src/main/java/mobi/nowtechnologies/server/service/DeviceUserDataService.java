package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class DeviceUserDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceUserDataService.class);

	private DeviceUserDataRepository deviceUserDataRepository;
	private UserService userService;
	private CommunityService communityService;

	public DeviceUserData getByXtifyToken(String token){
		return deviceUserDataRepository.findByXtifyToken(token);
	}

	public void saveXtifyToken(String xtifyToken, String userName, String communityName, String deviceUID) {
		User user = userService.findByNameAndCommunity(userName, communityName);
		Community community = communityService.getCommunityByName(communityName);
        String communityUrl = community.getRewriteUrlParameter();
        int userId = user.getId();

        DeviceUserData data = fillDeviceUserData(xtifyToken, deviceUID, communityUrl, userId);
        save(data);
    }

    private DeviceUserData fillDeviceUserData(String xtifyToken, String deviceUID, String communityUrl, int userId) {
        DeviceUserData data = deviceUserDataRepository.find(userId, communityUrl, deviceUID);
        if(data == null){
            data = new DeviceUserData(communityUrl, userId, xtifyToken, deviceUID);
        }
        data.setXtifyToken(xtifyToken);
        return data;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    private void save(DeviceUserData data) {
        try{
            deviceUserDataRepository.save(data);
        }catch (Exception e){
            LOGGER.warn("Duplicated xtify_token will not save to db:"+data, e.getMessage());
        }
    }

    public void setDeviceUserDataRepository(DeviceUserDataRepository deviceUserDataRepository) {
		this.deviceUserDataRepository = deviceUserDataRepository;
	}
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setCommunityService(CommunityService communityService) {
		this.communityService = communityService;
	}
	
}
