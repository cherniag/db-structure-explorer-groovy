package mobi.nowtechnologies.server.log4j;

import com.google.common.collect.Lists;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static mobi.nowtechnologies.server.shared.CollectionUtils.isEmpty;


/**
 * Created by Oleg Artomov on 6/20/2014.
 */
public class InMemoryEventAppender extends AppenderSkeleton {

    private Map<String, Collection<LoggingEvent>> map = new HashMap<String, Collection<LoggingEvent>>();


    @Override
    protected void append(LoggingEvent event) {
        Collection<LoggingEvent> events = map.get(event.getLoggerName());
        if (events == null) {
            events = Lists.newArrayList();
        }
        events.add(event);
        map.put(event.getLoggerName(), events);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public int countOfErrorsWithStackTraceForLogger(Class loggerClass) {
        return countOfInfoWithLevelWithStackTraceForLogger(Level.ERROR, loggerClass);
    }


    public int countOfWarnWithStackTraceForLogger(Class loggerClass) {
        return countOfInfoWithLevelWithStackTraceForLogger(Level.WARN, loggerClass);
    }


    public int totalCountOfMessagesWithStackTraceForException(Class throwableClass) {
        int result = 0;
        for (Map.Entry<String, Collection<LoggingEvent>> currentEntry : map.entrySet()) {
            for (LoggingEvent event : currentEntry.getValue()) {
                ThrowableInformation throwableInformation = event.getThrowableInformation();
                if (throwableInformation != null && throwableInformation.getThrowable().getClass() == throwableClass) {
                    result++;
                }
            }
        }
        return result;
    }

    private int countOfInfoWithLevelWithStackTraceForLogger(Level level, Class loggerClass) {
        Collection<LoggingEvent> events = map.get(loggerClass.getName());

        int result = 0;
        if (!isEmpty(events)) {
            for (LoggingEvent currentEvent : events) {
                if (currentEvent.getLevel().equals(level) && currentEvent.getThrowableInformation() != null) {
                    result++;
                }
            }
        }
        return result;
    }

}
