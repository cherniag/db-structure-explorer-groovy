package mobi.nowtechnologies.server.shared.log;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class ContextFilter extends Filter {
	private String contextKey;
	private String value;
	private boolean acceptOnMatch;

	public void setContextKey(String contextKey) {
		this.contextKey = contextKey;
	}

	public String getContextKey() {
		return contextKey;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean getAcceptOnMatch() {
		return acceptOnMatch;
	}

	public void setAcceptOnMatch(boolean acceptOnMatch) {
		this.acceptOnMatch = acceptOnMatch;
	}

	@Override
	public int decide(LoggingEvent loggingEvent) {
		CharSequence ctx = (CharSequence)loggingEvent.getMDC(contextKey);
		
		if (ctx == null)
			return DENY;
		if (value == null)
			return NEUTRAL;
		if (!value.contains(ctx))
			return DENY;
		return acceptOnMatch ? ACCEPT : DENY;
	}
}
