package mobi.nowtechnologies.applicationtests.features.serviceconfig.helpers;

import cucumber.api.Transformer;
import mobi.nowtechnologies.server.persistence.domain.versioncheck.ClientVersion;

public class ClientVersionTransformer extends Transformer<ClientVersion> {
    @Override
    public ClientVersion transform(String string) {
        return ClientVersion.from(string);
    }
}
