package mobi.nowtechnologies.applicationtests.features.social.email.transformers;


import cucumber.api.Transformer;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;
import mobi.nowtechnologies.applicationtests.services.helper.transformer.DataLoader;

import java.util.List;

public class VersionsTransformer extends Transformer<List<HasVersion>> {
    private final String location;

    public VersionsTransformer() {
        location = "features/email/versions.txt";
    }

    @Override
    public List<HasVersion> transform(String value) {
        return DataLoader.loadVersions(location);
    }
}
