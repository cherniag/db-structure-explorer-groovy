/**
 *
 */

package mobi.nowtechnologies.server.shared.log;

import static java.lang.String.valueOf;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Titov Mykhaylo (titov)
 */
public class UnixTimePatternLayout extends EnhancedPatternLayout {

    @Override
    public String format(LoggingEvent event) {
        long timeStamp = event.getTimeStamp();
        return super.format(event).replaceAll(" ut ", " " + valueOf(timeStamp) + " ");
    }

}
