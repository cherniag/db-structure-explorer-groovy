package mobi.nowtechnologies.server.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * User: Titov Mykhaylo (titov)
 * 27.09.13 12:57
 */
public abstract class ProviderUserDetails {

    public String operator;
    public String tariff;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("operator", operator)
                .append("tariff", tariff)
                .toString();
    }
}
