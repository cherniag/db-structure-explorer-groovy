package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository.ONE_RECORD_PAGEABLE;
import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.apache.commons.lang.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class StreamzineUpdateRepositoryIT extends AbstractRepositoryIT{
    @Resource
    private StreamzineUpdateRepository streamzineUpdateRepository;

    @Resource
    private UserRepository userRepository;

    @Test
    public void testSave() {
        Date startDate = addDays(new Date(), 5);

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
        Date startDate = addDays(new Date(), 5);
        Date lessDate = addDays(startDate, -1);
        Date moreDate = addDays(startDate, 1);

        Update underTest = buildUpdateEntity(startDate, null);
        Update update = streamzineUpdateRepository.saveAndFlush(underTest);

        List<Update> all = streamzineUpdateRepository.findAllByDate(lessDate, moreDate);

        assertEquals(1, all.size());
        assertEquals(update.getId(), all.get(0).getId());
    }

    @Test
     public void shouldFindSecondUpdateWhenItIsInPastAndThirdUpdateIsInTheFuture() throws Exception {
        //given
        Date firstUpdateDate = addDays(new Date(), 1);
        Date secondUpdateDate = addDays(firstUpdateDate, 1);
        Date thirdUpdateDate = addDays(secondUpdateDate, 1);

        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(firstUpdateDate, null));
        Update secondUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(secondUpdateDate, null));
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(thirdUpdateDate, null));

        //when
        Update update = streamzineUpdateRepository.findLatestUpdateBeforeDate(addHours(secondUpdateDate, 2));

        //then
        assertThat(update.getId(), is(secondUpdate.getId()));
    }

    @Test
    public void shouldFindSecondUpdateWhenItIsEqNowAndThirdUpdateIsInTheFuture() throws Exception {
        //given
        Date firstUpdateDate = addDays(new Date(), 1);
        Date secondUpdateDate = addDays(firstUpdateDate, 1);
        Date thirdUpdateDate = addDays(secondUpdateDate, 1);

        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(firstUpdateDate, null));
        Update secondUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(secondUpdateDate, null));
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(thirdUpdateDate, null));

        //when
        Update update = streamzineUpdateRepository.findLatestUpdateBeforeDate(secondUpdateDate);

        //then
        assertThat(update.getId(), is(secondUpdate.getId()));
    }

    @Test
    public void testFindLastSincePublished() throws Exception {
        final Date startDate = addDays(new Date(), 5);
        Date lessDate = addDays(startDate, -1);
        Date moreDate = addDays(startDate, 1);
        Date lastDate = addDays(moreDate, 1);

        Update entity = buildUpdateEntity(lessDate, null);

        Update lessUpdate = streamzineUpdateRepository.saveAndFlush(entity);
        Update moreUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(moreDate, null));
        Update lastUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(lastDate, null));

        List<Update> all = streamzineUpdateRepository.findLastSince(startDate, ONE_RECORD_PAGEABLE);
        assertEquals(1, all.size());
        assertEquals(lessUpdate.getId(), all.get(0).getId());
    }

    @Test
    public void testFindFirstAfterForUser() throws Exception {
        final Date dateZero = new Date();
        final Date updateDate = addDays(dateZero, 5);
        final Date dateToSearch = addDays(updateDate, 1);
        User user = UserFactory.createUser(ACTIVATED);
        user = userRepository.saveAndFlush(user);
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(updateDate, null));
        List<Update> all = streamzineUpdateRepository.findFirstAfterForUser(dateToSearch, user, ONE_RECORD_PAGEABLE);
        assertTrue(all.isEmpty());
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(updateDate, user));
        all = streamzineUpdateRepository.findFirstAfterForUser(dateZero, user, ONE_RECORD_PAGEABLE);
        assertEquals(1, all.size());

    }

    @Test
    public void testFindUpdatePublishDates() throws Exception {
        Date today = new Date();
        Date dateWithinMin = addDays(today, 1);
        Date dateWithinMax = addDays(today, 5);
        Date dateOutOf = addDays(today, 10);

        Update updateWithin1 = new Update(dateWithinMin);
        Update updateWithin2 = new Update(dateWithinMax);
        Update updateOutOf = new Update(dateOutOf);

        streamzineUpdateRepository.saveAndFlush(updateOutOf);
        streamzineUpdateRepository.saveAndFlush(updateWithin2);
        streamzineUpdateRepository.saveAndFlush(updateWithin1);

        Date from = addDays(dateWithinMin, -1);
        Date till = addDays(dateWithinMax, 1);
        List<Date> updatePublishDates = streamzineUpdateRepository.findUpdatePublishDates(from, till);

        assertThat(updatePublishDates, notNullValue());
        assertThat(updatePublishDates, hasSize(2));
        assertThat(updatePublishDates.get(0).getTime(), is(dateWithinMin.getTime()));
        assertThat(updatePublishDates.get(1).getTime(), is(dateWithinMax.getTime()));
    }

    private Update buildUpdateEntity(Date lessDate, User user) {
        Update result = new Update(lessDate);
        if (user != null){
           result.addUser(user);
        }
        return result;
    }
}