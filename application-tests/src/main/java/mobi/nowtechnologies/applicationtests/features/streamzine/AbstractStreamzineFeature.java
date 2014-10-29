package mobi.nowtechnologies.applicationtests.features.streamzine;

import mobi.nowtechnologies.applicationtests.features.common.client.MQAppClientDeviceSet;
import mobi.nowtechnologies.applicationtests.features.common.transformers.dictionary.Word;
import mobi.nowtechnologies.applicationtests.services.DbMediaService;
import mobi.nowtechnologies.applicationtests.services.RequestFormat;
import mobi.nowtechnologies.applicationtests.services.device.UserDeviceDataService;
import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.applicationtests.services.helper.UserDataCreator;
import mobi.nowtechnologies.applicationtests.services.http.streamzine.GetStreamzineHttpService;
import mobi.nowtechnologies.applicationtests.services.streamzine.StreamzineUpdateCreator;
import mobi.nowtechnologies.applicationtests.services.util.SimpleInterpolator;
import mobi.nowtechnologies.server.persistence.repository.ChartRepository;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractStreamzineFeature {
    @Resource
    UserDataCreator userDataCreator;
    @Resource
    StreamzineUpdateCreator streamzineUpdateCreator;
    @Resource
    GetStreamzineHttpService getStreamzineHttpService;
    @Resource
    UserDeviceDataService userDeviceDataService;
    @Resource
    SimpleInterpolator interpolator;
    @Resource
    CommunityRepository communityRepository;
    @Resource
    ChartRepository chartRepository;
    @Resource
    DbMediaService dbMediaService;

    @Resource
    MQAppClientDeviceSet deviceSet;

    protected List<UserDeviceData> currentUserDevices = new ArrayList<UserDeviceData>();

    protected List<UserDeviceData> initUserData(Set<RequestFormat> requestFormats, Word versions, Word communities, Word devices) {
        List<UserDeviceData> datas = userDeviceDataService.table(versions.list(), communities.set(), devices.set(), requestFormats);
        for (UserDeviceData data : datas) {
            deviceSet.singup(data);
            deviceSet.loginUsingFacebook(data);
        }
        return datas;
    }

    //
    // Helpers
    //
    protected String getErrorMessage(UserDeviceData data) {
        return "Failed to check for " + data;
    }

    protected Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

}