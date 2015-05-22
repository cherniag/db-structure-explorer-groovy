package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Block;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.DeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.deeplink.NotificationDeeplinkInfo;
import mobi.nowtechnologies.server.persistence.domain.streamzine.types.sub.LinkLocationType;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import static mobi.nowtechnologies.server.persistence.domain.streamzine.visual.ShapeType.SLIM_BANNER;

import javax.annotation.Resource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;

import org.springframework.transaction.annotation.Transactional;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/shared.xml", "/META-INF/service-test.xml", "/META-INF/dao-test.xml"})
@TransactionConfiguration(transactionManager = "persistence.TransactionManager", defaultRollback = true)
@Transactional
public class StreamzineUpdateServiceIT {

    @Resource
    public StreamzineUpdateService streamzineUpdateService;
    @Resource
    private StreamzineUpdateRepository streamzineUpdateRepository;
    @Resource
    private CommunityRepository communityRepository;

    @Test
    public void testCreateFirstTime() {
        final int firstDays = 10;
        final int avgDays = 11;
        final int secondDays = 12;

        Date now = new Date();
        Date firstDate = DateUtils.addDays(now, firstDays);
        Date avgDate = DateUtils.addDays(now, avgDays);
        Date secondDate = DateUtils.addDays(now, secondDays);

        long count = streamzineUpdateRepository.count();
        Assert.assertEquals(0, count);

        streamzineUpdateService.create(avgDate, findCommunity("hl_uk"));

        List<Update> allByDate = streamzineUpdateRepository.findAllByDate(firstDate, secondDate, findCommunity("hl_uk"));
        Assert.assertEquals(1, allByDate.size());
        Assert.assertEquals(avgDate.getTime(), allByDate.get(0).getDate().getTime());
    }

    @Test
    public void testCreateSecondTime() {
        Community community = findCommunity("hl_uk");

        final int firstDays = 10;
        final int avgDays = 14;
        final int secondDays = 28;

        Date now = new Date();
        Date firstDate = DateUtils.addDays(now, firstDays);
        Date avgDate = DateUtils.addDays(now, avgDays);
        Date secondDate = DateUtils.addDays(now, secondDays);

        final String title = "title";
        final Block block = createBlock(title);

        // create
        Update firstUpdate = streamzineUpdateService.create(avgDate, findCommunity("hl_uk"));
        // fill with blocks from UI
        streamzineUpdateService.update(firstUpdate.getId(), createWithBlock(firstUpdate, block, community));

        // and now create second time
        Date newDate = DateUtils.addDays(now, 25);
        Update secondUpdate = streamzineUpdateService.create(newDate, findCommunity("hl_uk"));

        List<Update> allByDate = streamzineUpdateRepository.findAllByDate(firstDate, secondDate, community);
        Assert.assertEquals(2, allByDate.size());
        // compare IDs
        Assert.assertEquals(firstUpdate.getId(), allByDate.get(0).getId());
        Assert.assertEquals(secondUpdate.getId(), allByDate.get(1).getId());

        // and compare blocks by title
        Assert.assertEquals(title, allByDate.get(0).getBlocks().get(0).getTitle());
        Assert.assertEquals(title, allByDate.get(1).getBlocks().get(0).getTitle());
    }

    @Test
    public void newUpdateShouldReplaceOldOneWithTheSameDate() throws Exception {
        long publishTimeMillis = System.currentTimeMillis() + 10000L;
        Date oldUpdateDate = new Date(publishTimeMillis);
        Date newUpdateDate = new Date(publishTimeMillis);
        Update oldUpdate = streamzineUpdateService.create(oldUpdateDate, findCommunity("hl_uk"));
        oldUpdate.addBlock(createBlock("title1"));
        oldUpdate.addBlock(createBlock("title2"));
        oldUpdate.addBlock(createBlock("title3"));
        streamzineUpdateService.update(oldUpdate.getId(), oldUpdate);
        Update newUpdate = streamzineUpdateService.createOrReplace(newUpdateDate, findCommunity("hl_uk"));
        List<Update> updates = Lists.newArrayList(streamzineUpdateService.list(newUpdateDate, findCommunity("hl_uk")));
        assertThat(oldUpdate.getId(), is(not(newUpdate.getId())));
        assertThat(updates.size(), is(1));
        assertThat(updates.get(0).getId(), is(newUpdate.getId()));
    }

    @Test
    public void testGetUpdatePublishDates() throws Exception {
        Community community = findCommunity("hl_uk");
        Date selectedUpdateDate = DateUtils.addDays(new Date(), 100);
        Date pastDateWithinInterval = DateUtils.addDays(selectedUpdateDate, -10);
        Date pastDateOutOfInterval = DateUtils.addDays(selectedUpdateDate, -50);
        Date futureDateWithinInterval = DateUtils.addDays(selectedUpdateDate, 10);
        Date futureDateOutOfInterval = DateUtils.addDays(selectedUpdateDate, 50);

        streamzineUpdateService.create(futureDateWithinInterval, community);
        streamzineUpdateService.create(futureDateOutOfInterval, community);
        streamzineUpdateService.create(pastDateOutOfInterval, community);
        streamzineUpdateService.create(pastDateWithinInterval, community);
        streamzineUpdateService.create(selectedUpdateDate, community);

        List<Date> updatesPublishTime = streamzineUpdateService.getUpdatePublishDates(selectedUpdateDate, community);
        assertThat(updatesPublishTime, notNullValue());

        assertThat(updatesPublishTime, hasSize(3));
        assertThat(DateUtils.truncate(updatesPublishTime.get(0), Calendar.DATE), is(DateUtils.truncate(pastDateWithinInterval, Calendar.DATE)));
        assertThat(DateUtils.truncate(updatesPublishTime.get(1), Calendar.DATE), is(DateUtils.truncate(selectedUpdateDate, Calendar.DATE)));
        assertThat(DateUtils.truncate(updatesPublishTime.get(2), Calendar.DATE), is(DateUtils.truncate(futureDateWithinInterval, Calendar.DATE)));
    }

    @Test
    public void testGetUpdatePublishDatesForMultipleUpdateWithinSameDay() throws Exception {
        Community community = findCommunity("hl_uk");

        Date calendarTime = normalizeToday();
        Date selectedUpdateDate = DateUtils.addDays(calendarTime, 100);
        Date day1 = DateUtils.addDays(selectedUpdateDate, 1);
        Date day2 = DateUtils.addDays(selectedUpdateDate, 2);

        streamzineUpdateService.create(new Date(day2.getTime() + 1000L), community);
        streamzineUpdateService.create(new Date(day2.getTime() + 2000L), community);
        streamzineUpdateService.create(new Date(day2.getTime() + 3000L), community);

        streamzineUpdateService.create(new Date(day1.getTime() + 1000L), community);
        streamzineUpdateService.create(new Date(day1.getTime() + 2000L), community);

        List<Date> updatesPublishTime = streamzineUpdateService.getUpdatePublishDates(selectedUpdateDate, community);
        assertThat(updatesPublishTime, notNullValue());

        assertThat(updatesPublishTime, hasSize(2));
        assertThat(DateUtils.truncate(updatesPublishTime.get(0), Calendar.DATE), is(DateUtils.truncate(day1, Calendar.DATE)));
        assertThat(DateUtils.truncate(updatesPublishTime.get(1), Calendar.DATE), is(DateUtils.truncate(day2, Calendar.DATE)));
    }

    private Date normalizeToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 5);
        return calendar.getTime();
    }

    private Update createWithBlock(Update from, Block block, Community community) {
        Update incoming = new Update(from.getDate(), community);
        for (User user : from.getUsers()) {
            incoming.addUser(user);
        }
        incoming.addBlock(block);

        return incoming;
    }

    private Block createBlock(String title) {
        DeeplinkInfo deeplink = new NotificationDeeplinkInfo(LinkLocationType.INTERNAL_AD, "about");
        Block block = new Block(0, SLIM_BANNER, deeplink);
        block.setTitle(title);
        return block;
    }

    @Test
    public void testIsAvailableOnlyForThisCommunities() {
        streamzineUpdateService.checkAvailability("mtv1");
        streamzineUpdateService.checkAvailability("hl_uk");
    }

    private Community findCommunity(String name) {
        return communityRepository.findByName(name);
    }
}
