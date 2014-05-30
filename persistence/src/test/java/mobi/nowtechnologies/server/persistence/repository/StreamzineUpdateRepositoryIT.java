package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository.ONE_RECORD_PAGEABLE;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertTrue;


public class StreamzineUpdateRepositoryIT extends AbstractRepositoryIT{
    @Resource
    private StreamzineUpdateRepository streamzineUpdateRepository;

    @Resource
    private UserRepository userRepository;

    @Test
    public void testSave() {
        Date startDate = DateUtils.addDays(new Date(), 5);

        Update underTest = buildUpdateEntity(startDate, null);
        underTest.addBlock(createBlock());
        Update update = streamzineUpdateRepository.saveAndFlush(underTest);
        assertTrue(update.getBlocks().get(0).getAccessPolicy().getId() > 0);
    }

    private Block createBlock() {
        Block b = new Block(0, ShapeType.WIDE, new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "app"));
        b.setAccessPolicy(AccessPolicy.enabledForVipOnly());
        return b;
    }

    @Test
    public void testFindAllByDate() {
        Date startDate = DateUtils.addDays(new Date(), 5);
        Date lessDate = DateUtils.addDays(startDate, -1);
        Date moreDate = DateUtils.addDays(startDate, 1);

        Update underTest = buildUpdateEntity(startDate, null);
        Update update = streamzineUpdateRepository.saveAndFlush(underTest);

        List<Update> all = streamzineUpdateRepository.findAllByDate(lessDate, moreDate);

        Assert.assertEquals(1, all.size());
        Assert.assertEquals(update.getId(), all.get(0).getId());
    }

    @Test
    public void testFindByMaxDate() throws Exception {
        final Date startDate = DateUtils.addDays(new Date(), 5);
        Date lessDate = DateUtils.addDays(startDate, -1);
        Date moreDate = DateUtils.addDays(startDate, 1);

        Update lessUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(lessDate, null));
        Update moreUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(moreDate, null));

        List<Update> all = streamzineUpdateRepository.findByMaxDate(ONE_RECORD_PAGEABLE);
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(moreUpdate.getId(), all.get(0).getId());
    }

    @Test
    public void testFindLastSincePublished() throws Exception {
        final Date startDate = DateUtils.addDays(new Date(), 5);
        Date lessDate = DateUtils.addDays(startDate, -1);
        Date moreDate = DateUtils.addDays(startDate, 1);
        Date lastDate = DateUtils.addDays(moreDate, 1);

        Update entity = buildUpdateEntity(lessDate, null);

        Update lessUpdate = streamzineUpdateRepository.saveAndFlush(entity);
        Update moreUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(moreDate, null));
        Update lastUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(lastDate, null));

        List<Update> all = streamzineUpdateRepository.findLastSince(startDate, ONE_RECORD_PAGEABLE);
        Assert.assertEquals(1, all.size());
        Assert.assertEquals(lessUpdate.getId(), all.get(0).getId());
    }

    @Test
    public void testFindLastSinceForUser() throws Exception {
        final Date dateZero = new Date();
        final Date updateDate = DateUtils.addDays(dateZero, 5);
        final Date dateToSearch = DateUtils.addDays(updateDate, 1);
        User user = UserFactory.createUser(ACTIVATED);
        user = userRepository.saveAndFlush(user);
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(updateDate, null));
        List<Update> all = streamzineUpdateRepository.findLastSinceForUser(dateToSearch, user, ONE_RECORD_PAGEABLE);
        assertTrue(all.isEmpty());
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(updateDate, user));
        all = streamzineUpdateRepository.findLastSinceForUser(dateToSearch, user, ONE_RECORD_PAGEABLE);
        Assert.assertEquals(1, all.size());

    }

    @Test
    public void testFindUpdatePublishDates() throws Exception {
        Date today = new Date();
        Date dateWithinMin = DateUtils.addDays(today, 1);
        Date dateWithinMax = DateUtils.addDays(today, 5);
        Date dateOutOf = DateUtils.addDays(today, 10);

        Update updateWithin1 = new Update(dateWithinMin);
        Update updateWithin2 = new Update(dateWithinMax);
        Update updateOutOf = new Update(dateOutOf);

        streamzineUpdateRepository.saveAndFlush(updateOutOf);
        streamzineUpdateRepository.saveAndFlush(updateWithin2);
        streamzineUpdateRepository.saveAndFlush(updateWithin1);

        Date from = DateUtils.addDays(dateWithinMin, -1);
        Date till = DateUtils.addDays(dateWithinMax, 1);
        List<Date> updatePublishDates = streamzineUpdateRepository.findUpdatePublishDates(from, till);

        assertThat(updatePublishDates, notNullValue());
        assertThat(updatePublishDates, hasSize(2));
        assertThat(updatePublishDates.get(0).getTime(), is(dateWithinMin.getTime()));
        assertThat(updatePublishDates.get(1).getTime(), is(dateWithinMax.getTime()));
    }

    private Update buildUpdateEntity(Date lessDate, User user) {
        Update result = new Update(lessDate);
        result.setUser(user);
        return result;
    }
}