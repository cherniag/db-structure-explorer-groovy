package mobi.nowtechnologies.server.shared.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Titov Mykhaylo (titov)
 */
public class ClassContextFilter extends Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassContextFilter.class);

    private String contextKey;
    private List<Class<?>> classes;
    private boolean acceptOnMatch;

    public String getContextKey() {
        return contextKey;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    public List<Class<?>> getClasses() {
        return classes;
    }

    public void setClassNames(String classNames) {
        String[] classNameArray = classNames.split(",");
        classes = new ArrayList<Class<?>>();
        for (int i = 0; i < classNameArray.length; i++) {
            try {
                Class<?> c = Class.forName(classNameArray[i].trim());
                classes.add(c);
            } catch (ClassNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public boolean getAcceptOnMatch() {
        return acceptOnMatch;
    }

    public void setAcceptOnMatch(boolean acceptOnMatch) {
        this.acceptOnMatch = acceptOnMatch;
    }

    @Override
    public int decide(LoggingEvent loggingEvent) {
        Class<?> ctx = (Class<?>) loggingEvent.getMDC(contextKey);

        if (ctx == null) {
            return DENY;
        }
        if (classes == null) {
            return NEUTRAL;
        }
        if (!classes.contains(ctx)) {
            return DENY;
        }
        return acceptOnMatch ?
               ACCEPT :
               DENY;
    }
}