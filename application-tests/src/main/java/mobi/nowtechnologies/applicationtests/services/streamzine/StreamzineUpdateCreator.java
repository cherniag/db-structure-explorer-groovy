package mobi.nowtechnologies.applicationtests.services.streamzine;

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
    public Update create(String communityUrl, Update update, int shiftSeconds) {
        Community c = communityRepository.findByRewriteUrlParameter(communityUrl);
        Update restored = new Update(DateUtils.addMilliseconds(new Date(), 1000), c);
        restored.updateFrom(update);
        restored.updateDate(DateUtils.addSeconds(new Date(), shiftSeconds));
        return streamzineUpdateRepository.saveAndFlush(restored);
    }

    @Transactional(value = "applicationTestsTransactionManager")
    public Update create(String communityUrl, int secondsInThePast, int plusSecondsInTheFuture) {
        Community c = communityRepository.findByRewriteUrlParameter(communityUrl);
        Update restored = new Update(DateUtils.addSeconds(new Date(), plusSecondsInTheFuture - secondsInThePast), c);
        return streamzineUpdateRepository.saveAndFlush(restored);
    }

    @Transactional(value = "applicationTestsTransactionManager")
    public void deleteAll() {
        for (Update update : streamzineUpdateRepository.findAll()) {
            streamzineUpdateRepository.delete(update.getId());
        }
    }
}
