package mobi.nowtechnologies.server.service.streamzine;

import com.google.common.collect.Lists;
import mobi.nowtechnologies.server.persistence.domain.User;
import mobi.nowtechnologies.server.persistence.domain.streamzine.Update;
import mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

import static mobi.nowtechnologies.server.persistence.repository.StreamzineUpdateRepository.ONE_RECORD_PAGEABLE;
import static org.springframework.util.CollectionUtils.isEmpty;

public class StreamzineUpdateService {
    private static final int REQUESTED_PERIOD_INTERVAL = 30;
    private StreamzineUpdateRepository streamzineUpdateRepository;

    @Transactional
    public Update create(Date date) {
        List<Update> lastOne = streamzineUpdateRepository.findByMaxDate(ONE_RECORD_PAGEABLE);

        Update clonedOrCreated = new Update(date);

        boolean firstUpdate = lastOne.isEmpty();
        if (!firstUpdate) {
            clonedOrCreated.cloneBlocks(lastOne.get(0));
        }

        return streamzineUpdateRepository.saveAndFlush(clonedOrCreated);
    }

    @Transactional
    public Update createOrReplace(Date date) {
        Update update = streamzineUpdateRepository.findByPublishDate(date);
        if (update != null) {
            streamzineUpdateRepository.delete(update);
        }
        return create(date);
    }

    public Collection<Update> list(Date rawDate) {
        Date from = nullifyTime(rawDate);
        Date till = DateUtils.addDays(from, 1);
        return streamzineUpdateRepository.findAllByDate(from, till);
    }

    public List<Date> getUpdatePublishDates(Date selectedDate){
        Date from = DateUtils.addDays(selectedDate, -REQUESTED_PERIOD_INTERVAL);
        Date till = DateUtils.addDays(selectedDate, REQUESTED_PERIOD_INTERVAL);
        List<Date> updatePublishDates = streamzineUpdateRepository.findUpdatePublishDates(from, till);
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

    public Update get(long id) {
        return streamzineUpdateRepository.findById(id);
    }

    public Update get(Date publishDate) {
        return streamzineUpdateRepository.findByPublishDate(publishDate);
    }

    @Transactional(readOnly = true)
    public Update getUpdate(Date date, User user) {
        Assert.notNull(date);
        Assert.notNull(user);

        List<Update> result = streamzineUpdateRepository.findLastSinceForUser(date, user, ONE_RECORD_PAGEABLE);
        if (isEmpty(result)){
            result = streamzineUpdateRepository.findLastSince(date, ONE_RECORD_PAGEABLE);
        }
        Assert.notEmpty(result, "No streamzine updates found for date: " + date);
        Update update = result.get(0);
        update.getBlocks().size();
        return update;
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
}
