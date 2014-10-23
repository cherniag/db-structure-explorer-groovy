package mobi.nowtechnologies.server.persistence.repository;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.UserFactory;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.AccessPolicy;
import mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static mobi.nowtechnologies.server.shared.enums.ActivationStatus.ACTIVATED;
import static org.apache.commons.lang.time.DateUtils.addDays;
import static org.apache.commons.lang.time.DateUtils.addHours;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;


public class StreamzineUpdateRepositoryIT extends AbstractRepositoryIT{
    @Resource
    private StreamzineUpdateRepository streamzineUpdateRepository;

    @Resource
    private UserRepository userRepository;
    @Resource
    private CommunityRepository communityRepository;

    @Test
    public void testSave() {
        Community community = findHlUkCommunity();
        Date startDate = addDays(new Date(), 5);

        Update underTest = buildUpdateEntity(startDate, null, community);
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
        Community community = findHlUkCommunity();
        Date startDate = addDays(new Date(), 5);
        Date lessDate = addDays(startDate, -1);
        Date moreDate = addDays(startDate, 1);

        Update underTest = buildUpdateEntity(startDate, null, community);
        Update update = streamzineUpdateRepository.saveAndFlush(underTest);

        List<Update> all = streamzineUpdateRepository.findAllByDate(lessDate, moreDate, community);

        assertEquals(1, all.size());
        assertEquals(update.getId(), all.get(0).getId());
    }

    @Test
     public void shouldFindSecondUpdateWhenItIsInPastAndThirdUpdateIsInTheFuture() throws Exception {
        //given
        Community community = findHlUkCommunity();
        Date firstUpdateDate = addDays(new Date(), 1);
        Date secondUpdateDate = addDays(firstUpdateDate, 1);
        Date thirdUpdateDate = addDays(secondUpdateDate, 1);

        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(firstUpdateDate, null, community));
        Update secondUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(secondUpdateDate, null, community));
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(thirdUpdateDate, null, community));

        //when
        Update update = streamzineUpdateRepository.findLatestUpdateBeforeDate(addHours(secondUpdateDate, 2), community);

        //then
        assertThat(update.getId(), is(secondUpdate.getId()));
    }

    @Test
    public void shouldFindSecondUpdateWhenItIsEqNowAndThirdUpdateIsInTheFuture() throws Exception {
        //given
        Community community = findHlUkCommunity();
        Date firstUpdateDate = addDays(new Date(), 1);
        Date secondUpdateDate = addDays(firstUpdateDate, 1);
        Date thirdUpdateDate = addDays(secondUpdateDate, 1);

        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(firstUpdateDate, null, community));
        Update secondUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(secondUpdateDate, null, community));
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(thirdUpdateDate, null, community));

        //when
        Update update = streamzineUpdateRepository.findLatestUpdateBeforeDate(secondUpdateDate, community);

        //then
        assertThat(update.getId(), is(secondUpdate.getId()));
    }

    @Test
    public void testfindLastDateSince() throws Exception {
        Community community = findHlUkCommunity();
        final Date startDate = addDays(new Date(), 5);
        Date lessDate = addDays(startDate, -1);
        Date moreDate = addDays(startDate, 1);
        Date lastDate = addDays(moreDate, 1);

        Update entity = buildUpdateEntity(lessDate, null, community);

        Update lessUpdate = streamzineUpdateRepository.saveAndFlush(entity);
        Update moreUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(moreDate, null, community));
        Update lastUpdate = streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(lastDate, null, community));

        Date date = streamzineUpdateRepository.findLastDateSince(startDate, community);
        Update update = streamzineUpdateRepository.findByPublishDate(date, community);
        assertEquals(lessUpdate.getId(), update.getId());
    }

    @Test
    public void testFindFirstAfterForUser() throws Exception {
        Community community = findHlUkCommunity();
        final Date dateZero = new Date();
        final Date updateDate = addDays(dateZero, 5);
        final Date dateToSearch = addDays(updateDate, 1);
        User user = UserFactory.createUser(ACTIVATED);
        user = userRepository.saveAndFlush(user);
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(updateDate, null, community));
        Date date = streamzineUpdateRepository.findFirstDateAfterForUser(dateToSearch, user, community);
        assertNull(date);
        Date updateDate1 = addDays(dateZero, 6);
        streamzineUpdateRepository.saveAndFlush(buildUpdateEntity(updateDate1, user, community));
        date  = streamzineUpdateRepository.findFirstDateAfterForUser(dateZero, user, community);
        assertEquals(updateDate1, date);
    }

    @Test
    public void testFindUpdatePublishDates() throws Exception {
        Community community = findHlUkCommunity();
        Date today = new Date();
        Date dateWithinMin = addDays(today, 1);
        Date dateWithinMax = addDays(today, 5);
        Date dateOutOf = addDays(today, 10);

        Update updateWithin1 = new Update(dateWithinMin, community);
        Update updateWithin2 = new Update(dateWithinMax, community);
        Update updateOutOf = new Update(dateOutOf, community);

        streamzineUpdateRepository.saveAndFlush(updateOutOf);
        streamzineUpdateRepository.saveAndFlush(updateWithin2);
        streamzineUpdateRepository.saveAndFlush(updateWithin1);

        Date from = addDays(dateWithinMin, -1);
        Date till = addDays(dateWithinMax, 1);
        List<Date> updatePublishDates = streamzineUpdateRepository.findUpdatePublishDates(from, till, community);

        assertThat(updatePublishDates, notNullValue());
        assertThat(updatePublishDates, hasSize(2));
        assertThat(updatePublishDates.get(0).getTime(), is(dateWithinMin.getTime()));
        assertThat(updatePublishDates.get(1).getTime(), is(dateWithinMax.getTime()));
    }

    private Update buildUpdateEntity(Date lessDate, User user, Community community) {
        Update result = new Update(lessDate, community);
        if (user != null){
           result.addUser(user);
        }
        return result;
    }

    private Community findHlUkCommunity() {
        return communityRepository.findByName("hl_uk");
    }
}