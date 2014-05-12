package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.DeviceUserData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.DeviceUserDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class DeviceUserDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceUserDataService.class);

	private DeviceUserDataRepository deviceUserDataRepository;

    @Transactional
	public void saveXtifyToken(User user, String token) {
        LOGGER.info("Saving xtify token [{}] for user id [{}]", token, user.getId());
        DeviceUserData found = findDataForUser(user);
        if(found == null) {
            deleteInCasesMergeUserOrTempRow(token);

            deviceUserDataRepository.save(new DeviceUserData(user, token));
        } else if(!found.getXtifyToken().equals(token)) {
            LOGGER.info("Update data [{}] with new token [{}]", found, token);
            found.setXtifyToken(token);
        }
    }

    private void deleteInCasesMergeUserOrTempRow(String token) {
        DeviceUserData found = deviceUserDataRepository.findByXtifyToken(token);
        if(found != null){
            LOGGER.info("Removing existing device user data [{}]", found);
            deviceUserDataRepository.removeByXtifyToken(token);
        }
    }

    private DeviceUserData findDataForUser(User user) {
        return deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
    }

    public void setDeviceUserDataRepository(DeviceUserDataRepository deviceUserDataRepository) {
		this.deviceUserDataRepository = deviceUserDataRepository;
	}

}
