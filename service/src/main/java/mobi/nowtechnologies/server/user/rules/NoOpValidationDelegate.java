package mobi.nowtechnologies.server.user.rules;

import org.apache.commons.lang.builder.ToStringBuilder;
import static org.apache.commons.lang.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class NoOpValidationDelegate implements ValidationDelegate {

    private final boolean validity;

    public NoOpValidationDelegate(boolean validity) {
        this.validity = validity;
    }

    @Override
    public boolean isValid() {
        return validity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE).append("validity", validity).toString();
    }
}
