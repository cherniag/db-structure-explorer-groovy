package mobi.nowtechnologies.applicationtests.services.streamzine;

import mobi.nowtechnologies.applicationtests.services.device.domain.UserDeviceData;
import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class StreamzineUpdateCreator {
    @Resource
    CommunityRepository communityRepository;
    @Resource
    StreamzineUpdateRepository streamzineUpdateRepository;

    @Transactional(value = "applicationTestsTransactionManager")
    public Update create(UserDeviceData data, Update update, int shiftSeconds) {
        Community c = communityRepository.findByRewriteUrlParameter(data.getCommunityUrl());
        Update restored = new Update(DateUtils.addMilliseconds(new Date(), 1000), c);
        restored.updateFrom(update);
        restored.updateDate(DateUtils.addSeconds(new Date(), shiftSeconds));
        return streamzineUpdateRepository.saveAndFlush(restored);
    }

    @Transactional(value = "applicationTestsTransactionManager")
    public Update create(UserDeviceData data, int shiftSeconds) {
        Community c = communityRepository.findByRewriteUrlParameter(data.getCommunityUrl());
        Update restored = new Update(DateUtils.addSeconds(new Date(), shiftSeconds+1), c);
        return streamzineUpdateRepository.saveAndFlush(restored);
    }
}
