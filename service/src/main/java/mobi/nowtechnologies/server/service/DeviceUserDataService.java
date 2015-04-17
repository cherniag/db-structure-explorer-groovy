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
        if (found == null) {
            deleteInCasesMergeUserOrTempRow(token);

            deviceUserDataRepository.save(new DeviceUserData(user, token));
        } else if (!found.getXtifyToken().equals(token)) {
            LOGGER.info("Update data [{}] with new token [{}]", found, token);
            deleteInCasesMergeUserOrTempRow(token);

            found.setXtifyToken(token);
        }
    }

    @Transactional
    public void removeDeviceUserData(User user) {
        int count = deviceUserDataRepository.deleteByUser(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
        LOGGER.info("Removed {} records for User[id={}, deviceUID={}, communityRewriteUrl={}]", count, user.getId(), user.getDeviceUID(), user.getCommunityRewriteUrl());
    }

    private void deleteInCasesMergeUserOrTempRow(String token) {
        DeviceUserData found = deviceUserDataRepository.findByXtifyToken(token);
        if (found != null) {
            LOGGER.info("Removing existing device user data [{}]", found);
            deviceUserDataRepository.deleteByXtifyToken(token);
        }
    }

    private DeviceUserData findDataForUser(User user) {
        return deviceUserDataRepository.find(user.getId(), user.getCommunityRewriteUrl(), user.getDeviceUID());
    }

    public void setDeviceUserDataRepository(DeviceUserDataRepository deviceUserDataRepository) {
        this.deviceUserDataRepository = deviceUserDataRepository;
    }

}
