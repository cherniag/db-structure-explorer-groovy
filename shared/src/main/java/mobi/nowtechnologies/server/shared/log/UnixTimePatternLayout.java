/**
 * 
 */
package mobi.nowtechnologies.server.shared.log;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Titov Mykhaylo (titov)
 * 
 */
public class UnixTimePatternLayout extends PatternLayout {
	@Override
	public String format(LoggingEvent event) {
		long timeStamp = event.getTimeStamp();
		return super.format(event).replaceAll(" ut ",
				" " + String.valueOf(timeStamp) + " ");
	}

}
