package mobi.nowtechnologies.applicationtests.features.common;

import cucumber.api.Transformer;
import mobi.nowtechnologies.applicationtests.services.device.domain.ApiVersion;
import mobi.nowtechnologies.applicationtests.services.device.domain.HasVersion;

public class VersionTransformer extends Transformer<HasVersion> {
    @Override
    public HasVersion transform(String value) {
        return ApiVersion.from(value);
    }
}
