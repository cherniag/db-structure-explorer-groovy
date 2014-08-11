package mobi.nowtechnologies.applicationtests.services.device.domain;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class ApiVersions {
    private List<ApiVersion> versions = new ArrayList<ApiVersion>();

    private ApiVersions() {
    }

    public static ApiVersions from(String manyVersions) {
        TreeSet<String> versions = Sets.newTreeSet(Splitter.on(",").omitEmptyStrings().split(manyVersions));

        ApiVersions apiVersions = new ApiVersions();

        for (String version : versions) {
            apiVersions.versions.add(ApiVersion.from(version));
        }

        return apiVersions;
    }

    public List<ApiVersion> above(ApiVersion of) {
        int indexOf = versions.indexOf(of);
        return new ArrayList<ApiVersion>(versions.subList(indexOf, versions.size() - 1));
    }
}
