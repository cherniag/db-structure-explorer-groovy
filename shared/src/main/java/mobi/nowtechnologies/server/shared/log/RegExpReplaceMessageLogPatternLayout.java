package mobi.nowtechnologies.server.shared.log;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * @author Titov Mykhaylo (titov)
 *
 */
public class RegExpReplaceMessageLogPatternLayout extends PatternLayout {
	
	private String[] messageKeys = new String[0];
	
	public void setMessageKeys(String[] messageKeys) {
		this.messageKeys = messageKeys;
	}
	
	@Override
	public String format(LoggingEvent loggingEvent) {
		String formatedMessage = super.format(loggingEvent);
		
		return formatedMessage;
	}
	

}
