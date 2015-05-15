package mobi.nowtechnologies.server.service.streamzine;

import mobi.nowtechnologies.server.persistence.domain.Community;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.repository.CommunityRepository;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import static mobi.nowtechnologies.server.shared.ObjectUtils.isNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import org.apache.commons.lang.time.DateUtils;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class StreamzineUpdateService {

    private static final int REQUESTED_PERIOD_INTERVAL = 30;
    private StreamzineUpdateRepository streamzineUpdateRepository;
    private CommunityRepository communityRepository;
    private List<String> availableCommunites = new ArrayList<String>();

    //
    // API
    //

    //
    // Availability
    //
    public boolean isAvailable(String community) {
        return availableCommunites.contains(community);
    }

    public void checkAvailability(String community) {
        if (!isAvailable(community)) {
            throw new StreamzineNotAvailable(community, availableCommunites);
        }
    }

    //
    // CRUD
    //
    @Transactional
    public Update create(Date date, Community community) {
        Assert.isTrue(isAvailable(community.getRewriteUrlParameter()), "Not available for " + community.getRewriteUrlParameter() + ", allowed for: " + availableCommunites);

        Update lastOne = findLatestUpdate(date, community);

        Update clonedOrCreated = new Update(date, community);

        boolean firstUpdate = isNull(lastOne);
        if (!firstUpdate) {
            clonedOrCreated.cloneBlocks(lastOne);
            clonedOrCreated.copyUsers(lastOne);
        }

        return streamzineUpdateRepository.saveAndFlush(clonedOrCreated);
    }

    private Update findLatestUpdate(Date date, Community community) {
        Date lastDateSince = streamzineUpdateRepository.findLastDateSince(date, community);
        return lastDateSince != null ?
               streamzineUpdateRepository.findByPublishDate(lastDateSince, community) :
               null;
    }

    @Transactional
    public Update createOrReplace(Date date, Community community) {
        Update update = streamzineUpdateRepository.findByPublishDate(date, community);
        if (update != null) {
            streamzineUpdateRepository.delete(update);
        }
        return create(date, community);
    }

    public Collection<Update> list(Date rawDate, Community community) {
        Date from = nullifyTime(rawDate);
        Date till = DateUtils.addDays(from, 1);
        return streamzineUpdateRepository.findAllByDate(from, till, community);
    }

    public List<Date> getUpdatePublishDates(Date selectedDate, Community community) {
        Date from = DateUtils.addDays(selectedDate, -REQUESTED_PERIOD_INTERVAL);
        Date till = DateUtils.addDays(selectedDate, REQUESTED_PERIOD_INTERVAL);
        List<Date> updatePublishDates = streamzineUpdateRepository.findUpdatePublishDates(from, till, community);
        return groupByDate(updatePublishDates);
    }

    private List<Date> groupByDate(List<Date> updatePublishDates) {
        Set<Date> dates = new TreeSet<Date>();
        for (Date updatePublishDate : updatePublishDates) {
            dates.add(nullifyTime(updatePublishDate));
        }
        return Lists.newArrayList(dates);
    }

    @Transactional
    public void delete(long id) {
        Update update = streamzineUpdateRepository.findOne(id);

        Assert.isTrue(update.canEdit());

        streamzineUpdateRepository.delete(id);
    }

    @Transactional
    public void update(long id, Update incoming) {
        Update existing = streamzineUpdateRepository.findOne(id);

        Assert.notNull(existing);
        Assert.isTrue(existing.canEdit());

        existing.updateFrom(incoming);

        streamzineUpdateRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public Update get(long id) {
        Update update = streamzineUpdateRepository.findById(id);
        if (update != null) {
            update.getUsers().size();
        }
        return update;
    }

    public Update get(Date publishDate, Community community) {
        return streamzineUpdateRepository.findByPublishDate(publishDate, community);
    }

    @Transactional(readOnly = true)
    public Update getUpdate(Date date, User user, String community) {
        Community c = communityRepository.findByRewriteUrlParameter(community);

        Assert.notNull(date);
        Assert.notNull(user);

        Date dateOfUpdate = calculateDate(user, c);
        Assert.notNull(dateOfUpdate, "No streamzine updates found for date: " + date);
        Update result = streamzineUpdateRepository.findByPublishDate(dateOfUpdate, c);
        result.getBlocks().size();
        result.getUsers().size();
        return result;
    }

    private Date calculateDate(User user, Community c) {
        Date startDate = new Date();
        Date result = streamzineUpdateRepository.findFirstDateAfterForUser(startDate, user, c);
        if (result == null) {
            result = streamzineUpdateRepository.findLastDateSince(startDate, c);
        }
        return result;
    }

    private Date nullifyTime(Date from) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.set(Calendar.AM_PM, Calendar.AM);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public void setStreamzineUpdateRepository(StreamzineUpdateRepository streamzineUpdateRepository) {
        this.streamzineUpdateRepository = streamzineUpdateRepository;
    }

    public void setAvailableCommunites(String... availableCommunites) {
        this.availableCommunites = Arrays.asList(availableCommunites);
    }

    public void setCommunityRepository(CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

}
