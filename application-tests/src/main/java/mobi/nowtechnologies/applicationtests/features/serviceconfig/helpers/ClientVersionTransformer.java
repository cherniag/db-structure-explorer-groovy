package mobi.nowtechnologies.applicationtests.features.serviceconfig.helpers;

import mobi.nowtechnologies.server.versioncheck.domain.ClientVersion;

import cucumber.api.Transformer;

public class ClientVersionTransformer extends Transformer<ClientVersion> {

    @Override
    public ClientVersion transform(String string) {
        return ClientVersion.from(string);
    }
}
