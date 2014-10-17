package mobi.nowtechnologies.server.log4j;

import com.google.common.base.Predicate;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by Oleg Artomov on 9/15/2014.
 */
public class PredicateByLevelAndThrowable implements Predicate<LoggingEvent> {

  private final Level level;

    public PredicateByLevelAndThrowable(Level level) {
        this.level = level;
    }

    @Override
    public boolean apply(LoggingEvent input) {
        return input.getLevel().equals(level) && input.getThrowableInformation() != null;
    }
}