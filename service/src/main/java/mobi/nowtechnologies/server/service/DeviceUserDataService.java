package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;

public class DeviceUserDataService {

	private DeviceUserDataRepository deviceUserDataRepository;
	private UserService userService;
	private CommunityService communityService;

	public DeviceUserData getByXtifyToken(String token){
		return deviceUserDataRepository.findByXtifyToken(token);
	}
	
	public void saveXtifyToken(String xtifyToken, String userName, String communityName, String deviceUID) {
		User user = userService.findByNameAndCommunity(userName, communityName);
		Community community = communityService.getCommunityByName(communityName);
		
		DeviceUserData data = new DeviceUserData();
		data.setDeviceUID(deviceUID);
		data.setUserId(user.getId());
		data.setXtifyToken(xtifyToken);
		data.setCommunityUrl(community.getRewriteUrlParameter());
		deviceUserDataRepository.save(data);
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
