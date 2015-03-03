package mobi.nowtechnologies.server.service.data;

import mobi.nowtechnologies.server.shared.enums.ProviderType;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * User: Alexsandr_Kolpakov Date: 10/2/13 Time: 12:16 PM
 */
public class SubscriberData {

    private String phoneNumber;
    private ProviderType provider;

    public ProviderType getProvider() {
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public SubscriberData withPhoneNumber(String phoneNumber) {
        setPhoneNumber(phoneNumber);

        return this;
    }

    public SubscriberData withProvider(ProviderType provider) {
        setProvider(provider);

        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("phoneNumber", phoneNumber).append("provider", provider).toString();
    }
}
