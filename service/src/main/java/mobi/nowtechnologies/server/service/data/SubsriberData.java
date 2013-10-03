package mobi.nowtechnologies.server.service.data;

import mobi.nowtechnologies.server.persistence.domain.enums.ProviderType;

/**
 * Created with IntelliJ IDEA.
 * User: Alexsandr_Kolpakov
 * Date: 10/2/13
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubsriberData {
    private ProviderType provider;

    public ProviderType getProvider(){
        return provider;
    }

    public void setProvider(ProviderType provider) {
        this.provider = provider;
    }

    public SubsriberData withProvider(ProviderType provider) {
        setProvider(provider);

        return this;
    }
}
