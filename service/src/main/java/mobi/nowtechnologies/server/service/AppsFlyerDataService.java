package mobi.nowtechnologies.server.service;

import mobi.nowtechnologies.server.persistence.domain.AppsFlyerData;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.repository.AppsFlyerDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Author: Gennadii Cherniaiev
 * Date: 11/10/2014
 */
public class AppsFlyerDataService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private AppsFlyerDataRepository appsFlyerDataRepository;

    @Transactional
    public void saveAppsFlyerData(User user, String appsFlyerUid) {
        logger.info("Trying to save apps flyer token [{}] for user id [{}]", appsFlyerUid, user.getId());
        AppsFlyerData found = appsFlyerDataRepository.findDataByUserId(user.getId());
        logger.info("Found data: [{}]", found);
        if(found == null){
            found = new AppsFlyerData(user.getId(), appsFlyerUid);
            appsFlyerDataRepository.save(found);
        } else {
            found.setAppsFlyerUid(appsFlyerUid);
        }
    }

    @Transactional
    public void mergeAppsFlyerData(User fromUser, User toUser) {
        AppsFlyerData fromData = appsFlyerDataRepository.findDataByUserId(fromUser.getId());
        AppsFlyerData toData = appsFlyerDataRepository.findDataByUserId(toUser.getId());
        logger.info("Trying to merge apps flyer data from [{}] to [{}]", fromData, toData);
        if(fromData == null && toData == null){
            return;
        }
        if(fromData != null){
            mergeData(fromData, toData, toUser);
        }
    }

    private void mergeData(AppsFlyerData fromData, AppsFlyerData toData, User toUser) {
        appsFlyerDataRepository.delete(fromData);
        appsFlyerDataRepository.flush();
        if(toData == null){
            toData = new AppsFlyerData(toUser.getId(), fromData.getAppsFlyerUid());
            appsFlyerDataRepository.save(toData);
        } else {
            toData.setAppsFlyerUid(fromData.getAppsFlyerUid());
        }
        logger.info("Saving result data [{}]", toData);
    }
}
