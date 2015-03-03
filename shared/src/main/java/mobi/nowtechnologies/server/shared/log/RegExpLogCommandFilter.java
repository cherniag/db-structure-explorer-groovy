package mobi.nowtechnologies.server.shared.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Titov Mykhaylo (titov)
 */
public class RegExpLogCommandFilter extends Filter {

    private String logCommandRegExp;
    private Pattern logCommandPattern;

    private int onMatch = NEUTRAL;
    private int onMismatch = DENY;

    public void setLogCommandRegExp(String logCommandRegExp) {
        this.logCommandRegExp = logCommandRegExp;
        logCommandPattern = Pattern.compile(logCommandRegExp);
    }

    public void setOnMatch(int onMatch) {
        this.onMatch = onMatch;
    }

    public void setOnMismatch(int onMismatch) {
        this.onMismatch = onMismatch;
    }

    @Override
    public int decide(LoggingEvent loggingEvent) {
        String logCommand = (String) loggingEvent.getMDC(LogUtils.LOG_COMMAND);
        Matcher matcher = logCommandPattern.matcher(logCommand);

        final int decision;

        if (matcher.matches()) {
            decision = onMatch;
        }
        else {
            decision = onMismatch;
        }

        return decision;
    }

}
