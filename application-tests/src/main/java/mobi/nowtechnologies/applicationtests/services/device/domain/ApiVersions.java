package mobi.nowtechnologies.applicationtests.services.device.domain;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ApiVersions {
    private List<String> versions = new ArrayList<String>();

    private ApiVersions() {
    }

    public static ApiVersions from(Collection<String> versions) {
        ApiVersions apiVersions = new ApiVersions();
        apiVersions.versions.addAll(versions);
        Collections.sort(apiVersions.versions);
        return apiVersions;
    }

    public List<String> above(String of) {
        int indexOf = versions.indexOf(of);
        Assert.isTrue(indexOf >= 0, "Not found version " + of + " in (" + versions + ")");
        return new ArrayList<String>(versions.subList(indexOf, versions.size()));
    }
}
